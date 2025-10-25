package com.app.persistence.data.remote.dto

import com.app.persistence.domain.model.PlaybackFormat
import com.app.persistence.domain.model.PlaybackLink
import com.app.persistence.domain.model.VideoQuality
import com.google.gson.annotations.SerializedName

data class PlaybackLinkDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("url")
    val url: String,
    
    @SerializedName("quality")
    val quality: String,
    
    @SerializedName("format")
    val format: String,
    
    @SerializedName("video_id")
    val videoId: String,
    
    @SerializedName("headers")
    val headers: Map<String, String>? = null,
    
    @SerializedName("expires_at")
    val expiresAt: Long? = null,
    
    @SerializedName("requires_auth")
    val requiresAuth: Boolean = false,
    
    @SerializedName("metadata")
    val metadata: Map<String, String>? = null
) {
    fun toDomain(): PlaybackLink {
        return PlaybackLink(
            id = id,
            url = url,
            quality = parseQuality(quality),
            format = parseFormat(format),
            videoId = videoId,
            headers = headers ?: emptyMap(),
            expiresAt = expiresAt,
            requiresAuth = requiresAuth,
            metadata = metadata ?: emptyMap()
        )
    }
    
    private fun parseQuality(quality: String): VideoQuality {
        return when (quality.uppercase()) {
            "LOW", "360P" -> VideoQuality.LOW
            "MEDIUM", "480P" -> VideoQuality.MEDIUM
            "HIGH", "720P" -> VideoQuality.HIGH
            "ULTRA", "1080P", "4K" -> VideoQuality.ULTRA
            else -> VideoQuality.MEDIUM
        }
    }
    
    private fun parseFormat(format: String): PlaybackFormat {
        return when (format.uppercase()) {
            "MP4" -> PlaybackFormat.MP4
            "HLS", "M3U8" -> PlaybackFormat.HLS
            "DASH", "MPD" -> PlaybackFormat.DASH
            "WEBM" -> PlaybackFormat.WEBM
            else -> PlaybackFormat.OTHER
        }
    }
}
