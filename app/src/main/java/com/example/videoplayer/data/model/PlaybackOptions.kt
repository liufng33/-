package com.example.videoplayer.data.model

sealed class PlaybackOptions {
    data class Success(
        val title: String,
        val qualities: List<StreamQuality>,
        val thumbnailUrl: String? = null
    ) : PlaybackOptions()

    data class Error(
        val message: String,
        val fallbackUrl: String? = null
    ) : PlaybackOptions()

    object Loading : PlaybackOptions()

    object Idle : PlaybackOptions()
}
