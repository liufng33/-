package com.example.videoplayer.data.parser

import com.example.videoplayer.data.model.PlaybackOptions
import com.example.videoplayer.data.model.StreamQuality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class DirectUrlParser @Inject constructor() : VideoParser {
    override val name: String = ParserSource.DIRECT.displayName

    override suspend fun parse(url: String): PlaybackOptions = withContext(Dispatchers.IO) {
        try {
            if (isDirectVideoUrl(url)) {
                val quality = detectQualityFromUrl(url)
                PlaybackOptions.Success(
                    title = extractFilenameFromUrl(url),
                    qualities = listOf(
                        StreamQuality(
                            quality = quality,
                            url = url,
                            format = extractFormatFromUrl(url)
                        )
                    )
                )
            } else {
                PlaybackOptions.Error(
                    message = "URL does not appear to be a direct video link",
                    fallbackUrl = null
                )
            }
        } catch (e: Exception) {
            PlaybackOptions.Error(
                message = "Failed to validate URL: ${e.message}",
                fallbackUrl = null
            )
        }
    }

    private fun isDirectVideoUrl(url: String): Boolean {
        val videoExtensions = listOf(".mp4", ".m3u8", ".webm", ".mkv", ".avi", ".mov")
        return videoExtensions.any { url.contains(it, ignoreCase = true) }
    }

    private fun detectQualityFromUrl(url: String): String {
        return when {
            url.contains("1080", ignoreCase = true) -> StreamQuality.QUALITY_1080P
            url.contains("720", ignoreCase = true) -> StreamQuality.QUALITY_720P
            url.contains("480", ignoreCase = true) -> StreamQuality.QUALITY_480P
            url.contains("360", ignoreCase = true) -> StreamQuality.QUALITY_360P
            else -> StreamQuality.QUALITY_AUTO
        }
    }

    private fun extractFormatFromUrl(url: String): String {
        return url.substringAfterLast(".", "unknown").substringBefore("?").lowercase()
    }

    private fun extractFilenameFromUrl(url: String): String {
        return try {
            URL(url).path.substringAfterLast("/").substringBefore("?")
        } catch (e: Exception) {
            "Video"
        }
    }
}
