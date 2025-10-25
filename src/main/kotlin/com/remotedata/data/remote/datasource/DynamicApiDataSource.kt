package com.remotedata.data.remote.datasource

import com.remotedata.data.remote.api.DynamicApiService
import com.remotedata.utils.RateLimiter
import com.remotedata.utils.RemoteException
import com.remotedata.utils.Result
import com.remotedata.utils.safeApiCall
import io.github.oshai.kotlinlogging.KotlinLogging
import retrofit2.Response

private val logger = KotlinLogging.logger {}

interface DynamicApiDataSource {
    suspend fun fetchDynamicContent(url: String): Result<String>
}

class DynamicApiDataSourceImpl(
    private val dynamicApiService: DynamicApiService,
    private val rateLimiter: RateLimiter
) : DynamicApiDataSource {
    
    override suspend fun fetchDynamicContent(url: String): Result<String> = rateLimiter.execute("dynamic-$url") {
        logger.info { "Fetching dynamic content from: $url" }
        
        safeApiCall {
            val response = dynamicApiService.fetchDynamicContent(url)
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
