package com.app.persistence.data.remote.source

import com.app.persistence.data.remote.api.SearchApiService
import com.app.persistence.data.remote.dto.SearchResponseDto
import com.app.persistence.domain.model.DataError
import com.app.persistence.domain.model.Result
import com.app.persistence.domain.model.Source
import javax.inject.Inject

class SearchRemoteDataSource @Inject constructor(
    private val searchApiService: SearchApiService
) {
    suspend fun search(
        source: Source,
        query: String,
        limit: Int,
        offset: Int,
        filters: Map<String, String>
    ): Result<SearchResponseDto> {
        return try {
            val response = searchApiService.search(
                url = "${source.baseUrl}/search",
                query = query,
                limit = limit,
                offset = offset,
                filters = filters
            )
            
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error(DataError.ParseError("Empty response body"))
            } else {
                Result.Error(handleHttpError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            Result.Error(translateException(e))
        }
    }
    
    suspend fun healthCheck(source: Source): Result<Boolean> {
        return try {
            val response = searchApiService.search(
                url = "${source.baseUrl}/health",
                query = "",
                limit = 1,
                offset = 0
            )
            Result.Success(response.isSuccessful)
        } catch (e: Exception) {
            Result.Success(false)
        }
    }
    
    private fun handleHttpError(code: Int, message: String): DataError {
        return when (code) {
            401, 403 -> DataError.AuthenticationError("Authentication failed: $message")
            404 -> DataError.NotFoundError("Resource not found: $message")
            429 -> DataError.RateLimitError("Rate limit exceeded: $message")
            in 400..499 -> DataError.ValidationError("Client error: $message")
            in 500..599 -> DataError.NetworkError("Server error: $message")
            else -> DataError.UnknownError("HTTP error $code: $message")
        }
    }
    
    private fun translateException(e: Exception): DataError {
        return when (e) {
            is java.net.UnknownHostException -> DataError.NetworkError("No internet connection", e)
            is java.net.SocketTimeoutException -> DataError.NetworkError("Request timeout", e)
            is java.io.IOException -> DataError.NetworkError("Network error: ${e.message}", e)
            is com.google.gson.JsonSyntaxException -> DataError.ParseError("Invalid JSON response", e)
            else -> DataError.UnknownError("Unknown error: ${e.message}", e)
        }
    }
}
