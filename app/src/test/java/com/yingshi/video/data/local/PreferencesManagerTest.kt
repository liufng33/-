package com.yingshi.video.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yingshi.video.data.local.preferences.PreferencesManager
import com.yingshi.video.domain.model.DarkMode
import com.yingshi.video.domain.model.PlaybackQuality
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PreferencesManagerTest {

    private lateinit var context: Context
    private lateinit var preferencesManager: PreferencesManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        preferencesManager = PreferencesManager(context)
    }

    @Test
    fun defaultPreferencesAreCorrect() = runTest {
        val prefs = preferencesManager.preferencesFlow.first()
        
        assert(prefs.lastSelectedSearchSourceId == null)
        assert(prefs.lastSelectedParserSourceId == null)
        assert(prefs.autoPlayNextVideo == false)
        assert(prefs.playbackSpeed == 1.0f)
        assert(prefs.playbackQuality == PlaybackQuality.AUTO)
        assert(prefs.useDataSaver == false)
        assert(prefs.showAdultContent == false)
        assert(prefs.darkMode == DarkMode.SYSTEM)
    }

    @Test
    fun setLastSelectedSearchSource() = runTest {
        preferencesManager.setLastSelectedSearchSource("source_123")
        val prefs = preferencesManager.preferencesFlow.first()
        
        assert(prefs.lastSelectedSearchSourceId == "source_123")
    }

    @Test
    fun setPlaybackSpeed() = runTest {
        preferencesManager.setPlaybackSpeed(1.5f)
        val prefs = preferencesManager.preferencesFlow.first()
        
        assert(prefs.playbackSpeed == 1.5f)
    }

    @Test
    fun setPlaybackQuality() = runTest {
        preferencesManager.setPlaybackQuality(PlaybackQuality.HIGH)
        val prefs = preferencesManager.preferencesFlow.first()
        
        assert(prefs.playbackQuality == PlaybackQuality.HIGH)
    }

    @Test
    fun setDarkMode() = runTest {
        preferencesManager.setDarkMode(DarkMode.DARK)
        val prefs = preferencesManager.preferencesFlow.first()
        
        assert(prefs.darkMode == DarkMode.DARK)
    }

    @Test
    fun setAutoPlayNextVideo() = runTest {
        preferencesManager.setAutoPlayNextVideo(true)
        val prefs = preferencesManager.preferencesFlow.first()
        
        assert(prefs.autoPlayNextVideo == true)
    }

    @Test
    fun clearAllPreferences() = runTest {
        preferencesManager.setLastSelectedSearchSource("source_123")
        preferencesManager.setPlaybackSpeed(2.0f)
        preferencesManager.clearAllPreferences()
        
        val prefs = preferencesManager.preferencesFlow.first()
        
        assert(prefs.lastSelectedSearchSourceId == null)
        assert(prefs.playbackSpeed == 1.0f)
    }
}
