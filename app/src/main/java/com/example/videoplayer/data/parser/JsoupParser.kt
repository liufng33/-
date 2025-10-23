package com.example.videoplayer.data.parser

import com.example.videoplayer.data.model.PlaybackOptions
import com.example.videoplayer.data.model.StreamQuality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

class JsoupParser @Inject constructor() : VideoParser {
    override val name: String = ParserSource.JSOUP.displayName

    override suspend fun parse(url: String): PlaybackOptions = withContext(Dispatchers.IO) {
        try {
            val document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get()

            val videoElements = document.select("video source, video")
            val qualities = mutableListOf<StreamQuality>()

            videoElements.forEach { element ->
                val videoUrl = element.attr("src").takeIf { it.isNotEmpty() }
                    ?: element.attr("data-src")
                
                if (videoUrl.isNotEmpty()) {
                    val quality = element.attr("data-quality").takeIf { it.isNotEmpty() }
                        ?: element.attr("label")
                        ?: detectQualityFromUrl(videoUrl)
                    
                    val format = element.attr("type").takeIf { it.isNotEmpty() }
                    
                    qualities.add(
                        StreamQuality(
                            quality = quality,
                            url = if (videoUrl.startsWith("http")) videoUrl else resolveUrl(url, videoUrl),
                            format = format
                        )
                    )
                }
            }

            // Try to find meta tags for Open Graph video
            if (qualities.isEmpty()) {
                val ogVideo = document.select("meta[property=og:video], meta[property=og:video:url]")
                    .firstOrNull()?.attr("content")
                
                if (!ogVideo.isNullOrEmpty()) {
                    qualities.add(
                        StreamQuality(
                            quality = StreamQuality.QUALITY_AUTO,
                            url = ogVideo
                        )
                    )
                }
            }

            // Try to find iframe embeds
            if (qualities.isEmpty()) {
                val iframeUrl = document.select("iframe[src*=video], iframe[src*=player]")
                    .firstOrNull()?.attr("src")
                
                if (!iframeUrl.isNullOrEmpty()) {
                    return@withContext PlaybackOptions.Error(
                        message = "Found embedded player, direct parsing not supported",
                        fallbackUrl = iframeUrl
                    )
                }
            }

            if (qualities.isNotEmpty()) {
                val title = document.select("meta[property=og:title]").firstOrNull()?.attr("content")
                    ?: document.title()
                    ?: "Video"
                
                val thumbnail = document.select("meta[property=og:image]").firstOrNull()?.attr("content")

                PlaybackOptions.Success(
                    title = title,
                    qualities = qualities.distinctBy { it.url },
                    thumbnailUrl = thumbnail
                )
            } else {
                PlaybackOptions.Error(
                    message = "No video sources found in the page",
                    fallbackUrl = null
                )
            }
        } catch (e: Exception) {
            PlaybackOptions.Error(
                message = "Failed to parse URL: ${e.message}",
                fallbackUrl = null
            )
        }
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

    private fun resolveUrl(baseUrl: String, relativeUrl: String): String {
        return try {
            java.net.URL(java.net.URL(baseUrl), relativeUrl).toString()
        } catch (e: Exception) {
            relativeUrl
        }
    }
}
