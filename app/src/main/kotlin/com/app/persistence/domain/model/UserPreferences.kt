package com.app.persistence.domain.model

data class UserPreferences(
    val lastSelectedSourceId: Long? = null,
    val autoPlayEnabled: Boolean = false,
    val playbackQuality: PlaybackQuality = PlaybackQuality.MEDIUM,
    val downloadQuality: PlaybackQuality = PlaybackQuality.HIGH,
    val downloadLocation: String = "",
    val autoDownloadEnabled: Boolean = false,
    val darkModeEnabled: Boolean = false
)

enum class PlaybackQuality {
    LOW,
    MEDIUM,
    HIGH,
    ULTRA
}
