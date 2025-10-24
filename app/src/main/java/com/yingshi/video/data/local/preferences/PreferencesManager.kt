package com.yingshi.video.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.yingshi.video.domain.model.AppPreferences
import com.yingshi.video.domain.model.DarkMode
import com.yingshi.video.domain.model.PlaybackQuality
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class PreferencesManager(private val context: Context) {
    
    private object PreferencesKeys {
        val LAST_SELECTED_SEARCH_SOURCE = stringPreferencesKey("last_selected_search_source")
        val LAST_SELECTED_PARSER_SOURCE = stringPreferencesKey("last_selected_parser_source")
        val AUTO_PLAY_NEXT_VIDEO = booleanPreferencesKey("auto_play_next_video")
        val PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
        val PLAYBACK_QUALITY = stringPreferencesKey("playback_quality")
        val USE_DATA_SAVER = booleanPreferencesKey("use_data_saver")
        val SHOW_ADULT_CONTENT = booleanPreferencesKey("show_adult_content")
        val DARK_MODE = stringPreferencesKey("dark_mode")
    }

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AppPreferences(
                lastSelectedSearchSourceId = preferences[PreferencesKeys.LAST_SELECTED_SEARCH_SOURCE],
                lastSelectedParserSourceId = preferences[PreferencesKeys.LAST_SELECTED_PARSER_SOURCE],
                autoPlayNextVideo = preferences[PreferencesKeys.AUTO_PLAY_NEXT_VIDEO] ?: false,
                playbackSpeed = preferences[PreferencesKeys.PLAYBACK_SPEED] ?: 1.0f,
                playbackQuality = PlaybackQuality.valueOf(
                    preferences[PreferencesKeys.PLAYBACK_QUALITY] ?: PlaybackQuality.AUTO.name
                ),
                useDataSaver = preferences[PreferencesKeys.USE_DATA_SAVER] ?: false,
                showAdultContent = preferences[PreferencesKeys.SHOW_ADULT_CONTENT] ?: false,
                darkMode = DarkMode.valueOf(
                    preferences[PreferencesKeys.DARK_MODE] ?: DarkMode.SYSTEM.name
                )
            )
        }

    suspend fun setLastSelectedSearchSource(sourceId: String?) {
        context.dataStore.edit { preferences ->
            if (sourceId != null) {
                preferences[PreferencesKeys.LAST_SELECTED_SEARCH_SOURCE] = sourceId
            } else {
                preferences.remove(PreferencesKeys.LAST_SELECTED_SEARCH_SOURCE)
            }
        }
    }

    suspend fun setLastSelectedParserSource(sourceId: String?) {
        context.dataStore.edit { preferences ->
            if (sourceId != null) {
                preferences[PreferencesKeys.LAST_SELECTED_PARSER_SOURCE] = sourceId
            } else {
                preferences.remove(PreferencesKeys.LAST_SELECTED_PARSER_SOURCE)
            }
        }
    }

    suspend fun setAutoPlayNextVideo(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_PLAY_NEXT_VIDEO] = enabled
        }
    }

    suspend fun setPlaybackSpeed(speed: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PLAYBACK_SPEED] = speed
        }
    }

    suspend fun setPlaybackQuality(quality: PlaybackQuality) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PLAYBACK_QUALITY] = quality.name
        }
    }

    suspend fun setUseDataSaver(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_DATA_SAVER] = enabled
        }
    }

    suspend fun setShowAdultContent(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_ADULT_CONTENT] = enabled
        }
    }

    suspend fun setDarkMode(mode: DarkMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = mode.name
        }
    }

    suspend fun clearAllPreferences() {
        context.dataStore.edit { it.clear() }
    }
}
