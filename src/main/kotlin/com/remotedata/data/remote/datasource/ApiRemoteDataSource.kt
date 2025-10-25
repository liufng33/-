package com.remotedata.data.remote.datasource

import com.remotedata.data.mapper.ApiResponseMapper.toDomain
import com.remotedata.data.remote.api.ApiService
import com.remotedata.domain.entity.ApiResult
import com.remotedata.utils.RateLimiter
import com.remotedata.utils.RemoteException
import com.remotedata.utils.Result
import com.remotedata.utils.safeApiCall
import io.github.oshai.kotlinlogging.KotlinLogging
import retrofit2.Response
import java.net.SocketTimeoutException

private val logger = KotlinLogging.logger {}

interface ApiRemoteDataSource {
    suspend fun fetchApiData(id: String): Result<ApiResult>
    suspend fun fetchRawData(id: String): Result<String>
}

class ApiRemoteDataSourceImpl(
    private val apiService: ApiService,
    private val rateLimiter: RateLimiter
) : ApiRemoteDataSource {
    
    override suspend fun fetchApiData(id: String): Result<ApiResult> = rateLimiter.execute("api") {
        logger.info { "Fetching API data for id: $id" }
        
        safeApiCall {
            val response = apiService.getApiData(id)
            handleResponse(response) { dto ->
                dto.toDomain()
            }
        }
    }
    
    override suspend fun fetchRawData(id: String): Result<String> = rateLimiter.execute("api") {
        logger.info { "Fetching raw API data for id: $id" }
        
        safeApiCall {
            val response = apiService.getApiDataAsString(id)
            handleResponse(response) { it }
        }
    }
    
    private fun <T, R> handleResponse(response: Response<T>, transform: (T) -> R): R {
        return when {
            response.isSuccessful -> {
                val body = response.body()
                if (body != null) {
                    transform(body)
                } else {
                    throw RemoteException.ParseError("Response body is null")
                }
            }
            response.code() == 429 -> {
                val retryAfter = response.headers()["Retry-After"]?.toLongOrNull()
                throw RemoteException.RateLimitError(retryAfter, "Rate limit exceeded")
            }
            response.code() in 400..499 -> {
                throw RemoteException.HttpError(response.code(), "Client error: ${response.code()}")
            }
            response.code() in 500..599 -> {
                throw RemoteException.HttpError(response.code(), "Server error: ${response.code()}")
            }
            else -> {
                throw RemoteException.HttpError(response.code(), "HTTP error: ${response.code()}")
            }
        }
    }
}
