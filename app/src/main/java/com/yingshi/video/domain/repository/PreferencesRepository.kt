package com.yingshi.video.domain.repository

import com.yingshi.video.domain.model.AppPreferences
import com.yingshi.video.domain.model.DarkMode
import com.yingshi.video.domain.model.PlaybackQuality
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val preferences: Flow<AppPreferences>

    suspend fun setLastSelectedSearchSource(sourceId: String?)
    suspend fun setLastSelectedParserSource(sourceId: String?)
    suspend fun setAutoPlayNextVideo(enabled: Boolean)
    suspend fun setPlaybackSpeed(speed: Float)
    suspend fun setPlaybackQuality(quality: PlaybackQuality)
    suspend fun setUseDataSaver(enabled: Boolean)
    suspend fun setShowAdultContent(enabled: Boolean)
    suspend fun setDarkMode(mode: DarkMode)
    suspend fun clearAll()
}
