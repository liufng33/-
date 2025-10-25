package com.app.persistence.data.remote.source

import com.app.persistence.data.remote.api.PlaybackApiService
import com.app.persistence.data.remote.dto.PlaybackLinkDto
import com.app.persistence.domain.model.DataError
import com.app.persistence.domain.model.PlaybackLink
import com.app.persistence.domain.model.Result
import com.app.persistence.domain.model.VideoItem
import javax.inject.Inject

class PlaybackRemoteDataSource @Inject constructor(
    private val playbackApiService: PlaybackApiService
) {
    suspend fun getPlaybackLinks(
        video: VideoItem,
        baseUrl: String
    ): Result<List<PlaybackLinkDto>> {
        return try {
            val response = playbackApiService.getPlaybackLinks(
                url = "$baseUrl/playback/${video.id}",
                videoId = video.id
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
    
    suspend fun refreshPlaybackLink(
        link: PlaybackLink,
        baseUrl: String
    ): Result<PlaybackLinkDto> {
        return try {
            val response = playbackApiService.refreshPlaybackLink(
                url = "$baseUrl/playback/refresh/${link.id}"
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
