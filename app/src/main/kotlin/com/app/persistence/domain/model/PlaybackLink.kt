package com.app.persistence.domain.model

data class PlaybackLink(
    val id: String,
    val url: String,
    val quality: VideoQuality,
    val format: PlaybackFormat,
    val videoId: String,
    val headers: Map<String, String> = emptyMap(),
    val expiresAt: Long? = null,
    val requiresAuth: Boolean = false,
    val metadata: Map<String, String> = emptyMap()
) {
    fun isExpired(): Boolean {
        return expiresAt?.let { it < System.currentTimeMillis() } ?: false
    }

    fun requiresAuthentication(): Boolean = requiresAuth
}

enum class VideoQuality {
    LOW, MEDIUM, HIGH, ULTRA
}

enum class PlaybackFormat {
    MP4, HLS, DASH, WEBM, OTHER
}
