package com.app.persistence.data.repository

import com.app.persistence.data.local.datastore.UserPreferencesDataStore
import com.app.persistence.domain.model.PlaybackQuality
import com.app.persistence.domain.model.UserPreferences
import com.app.persistence.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : PreferencesRepository {
    
    override val userPreferencesFlow: Flow<UserPreferences>
        get() = userPreferencesDataStore.userPreferencesFlow
    
    override suspend fun updateLastSelectedSourceId(sourceId: Long?) {
        userPreferencesDataStore.updateLastSelectedSourceId(sourceId)
    }
    
    override suspend fun updateAutoPlayEnabled(enabled: Boolean) {
        userPreferencesDataStore.updateAutoPlayEnabled(enabled)
    }
    
    override suspend fun updatePlaybackQuality(quality: PlaybackQuality) {
        userPreferencesDataStore.updatePlaybackQuality(quality)
    }
    
    override suspend fun updateDownloadQuality(quality: PlaybackQuality) {
        userPreferencesDataStore.updateDownloadQuality(quality)
    }
    
    override suspend fun updateDownloadLocation(location: String) {
        userPreferencesDataStore.updateDownloadLocation(location)
    }
    
    override suspend fun updateAutoDownloadEnabled(enabled: Boolean) {
        userPreferencesDataStore.updateAutoDownloadEnabled(enabled)
    }
    
    override suspend fun updateDarkModeEnabled(enabled: Boolean) {
        userPreferencesDataStore.updateDarkModeEnabled(enabled)
    }
    
    override suspend fun clearAll() {
        userPreferencesDataStore.clearAll()
    }
}
