package com.remotedata.data.remote.datasource

import com.remotedata.data.mapper.SearchResponseMapper.toDomain
import com.remotedata.data.remote.api.SearchService
import com.remotedata.domain.entity.SearchResult
import com.remotedata.utils.RateLimiter
import com.remotedata.utils.RemoteException
import com.remotedata.utils.Result
import com.remotedata.utils.safeApiCall
import io.github.oshai.kotlinlogging.KotlinLogging
import retrofit2.Response

private val logger = KotlinLogging.logger {}

interface SearchRemoteDataSource {
    suspend fun search(query: String, page: Int = 1, limit: Int = 10): Result<List<SearchResult>>
}

class SearchRemoteDataSourceImpl(
    private val searchService: SearchService,
    private val rateLimiter: RateLimiter
) : SearchRemoteDataSource {
    
    override suspend fun search(query: String, page: Int, limit: Int): Result<List<SearchResult>> = 
        rateLimiter.execute("search") {
            logger.info { "Searching for: $query (page: $page, limit: $limit)" }
            
            safeApiCall {
                val response = searchService.search(query, page, limit)
                handleResponse(response) { dto ->
                    dto.toDomain()
                }
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
