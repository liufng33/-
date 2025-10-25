package com.app.persistence.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.persistence.domain.model.PlaybackQuality
import com.app.persistence.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesDataStore(private val context: Context) {
    
    private object PreferencesKeys {
        val LAST_SELECTED_SOURCE_ID = longPreferencesKey("last_selected_source_id")
        val AUTO_PLAY_ENABLED = booleanPreferencesKey("auto_play_enabled")
        val PLAYBACK_QUALITY = stringPreferencesKey("playback_quality")
        val DOWNLOAD_QUALITY = stringPreferencesKey("download_quality")
        val DOWNLOAD_LOCATION = stringPreferencesKey("download_location")
        val AUTO_DOWNLOAD_ENABLED = booleanPreferencesKey("auto_download_enabled")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    }
    
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            lastSelectedSourceId = preferences[PreferencesKeys.LAST_SELECTED_SOURCE_ID],
            autoPlayEnabled = preferences[PreferencesKeys.AUTO_PLAY_ENABLED] ?: false,
            playbackQuality = preferences[PreferencesKeys.PLAYBACK_QUALITY]?.let {
                try {
                    PlaybackQuality.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    PlaybackQuality.MEDIUM
                }
            } ?: PlaybackQuality.MEDIUM,
            downloadQuality = preferences[PreferencesKeys.DOWNLOAD_QUALITY]?.let {
                try {
                    PlaybackQuality.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    PlaybackQuality.HIGH
                }
            } ?: PlaybackQuality.HIGH,
            downloadLocation = preferences[PreferencesKeys.DOWNLOAD_LOCATION] ?: "",
            autoDownloadEnabled = preferences[PreferencesKeys.AUTO_DOWNLOAD_ENABLED] ?: false,
            darkModeEnabled = preferences[PreferencesKeys.DARK_MODE_ENABLED] ?: false
        )
    }
    
    suspend fun updateLastSelectedSourceId(sourceId: Long?) {
        context.dataStore.edit { preferences ->
            if (sourceId != null) {
                preferences[PreferencesKeys.LAST_SELECTED_SOURCE_ID] = sourceId
            } else {
                preferences.remove(PreferencesKeys.LAST_SELECTED_SOURCE_ID)
            }
        }
    }
    
    suspend fun updateAutoPlayEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_PLAY_ENABLED] = enabled
        }
    }
    
    suspend fun updatePlaybackQuality(quality: PlaybackQuality) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PLAYBACK_QUALITY] = quality.name
        }
    }
    
    suspend fun updateDownloadQuality(quality: PlaybackQuality) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DOWNLOAD_QUALITY] = quality.name
        }
    }
    
    suspend fun updateDownloadLocation(location: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DOWNLOAD_LOCATION] = location
        }
    }
    
    suspend fun updateAutoDownloadEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_DOWNLOAD_ENABLED] = enabled
        }
    }
    
    suspend fun updateDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] = enabled
        }
    }
    
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
