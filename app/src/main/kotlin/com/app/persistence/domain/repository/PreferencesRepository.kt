package com.app.persistence.domain.repository

import com.app.persistence.domain.model.PlaybackQuality
import com.app.persistence.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    
    val userPreferencesFlow: Flow<UserPreferences>
    
    suspend fun updateLastSelectedSourceId(sourceId: Long?)
    
    suspend fun updateAutoPlayEnabled(enabled: Boolean)
    
    suspend fun updatePlaybackQuality(quality: PlaybackQuality)
    
    suspend fun updateDownloadQuality(quality: PlaybackQuality)
    
    suspend fun updateDownloadLocation(location: String)
    
    suspend fun updateAutoDownloadEnabled(enabled: Boolean)
    
    suspend fun updateDarkModeEnabled(enabled: Boolean)
    
    suspend fun clearAll()
}
