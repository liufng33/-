package com.example.videoplayer.data.model

data class StreamQuality(
    val quality: String,
    val url: String,
    val format: String? = null,
    val bitrate: Int? = null
) {
    companion object {
        const val QUALITY_1080P = "1080p"
        const val QUALITY_720P = "720p"
        const val QUALITY_480P = "480p"
        const val QUALITY_360P = "360p"
        const val QUALITY_AUTO = "Auto"
    }
}
