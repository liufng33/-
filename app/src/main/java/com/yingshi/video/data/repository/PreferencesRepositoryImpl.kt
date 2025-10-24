package com.yingshi.video.data.repository

import com.yingshi.video.data.local.preferences.PreferencesManager
import com.yingshi.video.domain.model.AppPreferences
import com.yingshi.video.domain.model.DarkMode
import com.yingshi.video.domain.model.PlaybackQuality
import com.yingshi.video.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : PreferencesRepository {

    override val preferences: Flow<AppPreferences>
        get() = preferencesManager.preferencesFlow

    override suspend fun setLastSelectedSearchSource(sourceId: String?) {
        preferencesManager.setLastSelectedSearchSource(sourceId)
    }

    override suspend fun setLastSelectedParserSource(sourceId: String?) {
        preferencesManager.setLastSelectedParserSource(sourceId)
    }

    override suspend fun setAutoPlayNextVideo(enabled: Boolean) {
        preferencesManager.setAutoPlayNextVideo(enabled)
    }

    override suspend fun setPlaybackSpeed(speed: Float) {
        preferencesManager.setPlaybackSpeed(speed)
    }

    override suspend fun setPlaybackQuality(quality: PlaybackQuality) {
        preferencesManager.setPlaybackQuality(quality)
    }

    override suspend fun setUseDataSaver(enabled: Boolean) {
        preferencesManager.setUseDataSaver(enabled)
    }

    override suspend fun setShowAdultContent(enabled: Boolean) {
        preferencesManager.setShowAdultContent(enabled)
    }

    override suspend fun setDarkMode(mode: DarkMode) {
        preferencesManager.setDarkMode(mode)
    }

    override suspend fun clearAll() {
        preferencesManager.clearAllPreferences()
    }
}
