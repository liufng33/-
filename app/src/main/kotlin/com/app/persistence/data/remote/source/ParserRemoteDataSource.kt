package com.app.persistence.data.remote.source

import com.app.persistence.data.remote.api.ParserApiService
import com.app.persistence.data.remote.api.ParseRequest
import com.app.persistence.data.remote.dto.VideoDto
import com.app.persistence.domain.model.DataError
import com.app.persistence.domain.model.ParserConfig
import com.app.persistence.domain.model.Result
import javax.inject.Inject

class ParserRemoteDataSource @Inject constructor(
    private val parserApiService: ParserApiService
) {
    suspend fun parseVideoPage(
        parser: ParserConfig,
        url: String
    ): Result<VideoDto> {
        return try {
            val baseUrl = parser.baseUrl ?: extractBaseUrl(url)
            val response = parserApiService.parseVideoPage(
                url = "$baseUrl/parse",
                request = ParseRequest(
                    url = url,
                    rules = parser.rules.associate { it.name to it.pattern }
                )
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
    
    private fun extractBaseUrl(url: String): String {
        return try {
            val uri = java.net.URI(url)
            "${uri.scheme}://${uri.host}"
        } catch (e: Exception) {
            url
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
