package com.yingshi.video.domain.model

data class AppPreferences(
    val lastSelectedSearchSourceId: String? = null,
    val lastSelectedParserSourceId: String? = null,
    val autoPlayNextVideo: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val playbackQuality: PlaybackQuality = PlaybackQuality.AUTO,
    val useDataSaver: Boolean = false,
    val showAdultContent: Boolean = false,
    val darkMode: DarkMode = DarkMode.SYSTEM
)

enum class PlaybackQuality {
    AUTO,
    LOW,
    MEDIUM,
    HIGH,
    ULTRA
}

enum class DarkMode {
    LIGHT,
    DARK,
    SYSTEM
}
