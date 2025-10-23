package com.example.videoplayer.ui

import android.content.Context
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.videoplayer.ui.player.PlayerController
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerControllerTest {

    private lateinit var context: Context
    private lateinit var playerController: PlayerController
    private lateinit var mockExoPlayer: ExoPlayer

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        mockExoPlayer = mockk(relaxed = true)
        
        mockkStatic(ExoPlayer::class)
        every { ExoPlayer.Builder(any()).build() } returns mockExoPlayer

        playerController = PlayerController(context)
    }

    @Test
    fun `initializePlayer creates ExoPlayer instance`() {
        val player = playerController.initializePlayer()
        
        assertNotNull(player)
        verify { mockExoPlayer.addListener(any()) }
    }

    @Test
    fun `initializePlayer returns same instance on subsequent calls`() {
        val player1 = playerController.initializePlayer()
        val player2 = playerController.initializePlayer()
        
        assertEquals(player1, player2)
    }

    @Test
    fun `prepare sets media item and prepares player`() {
        playerController.initializePlayer()
        val testUrl = "https://example.com/video.mp4"
        
        playerController.prepare(testUrl)
        
        verify { mockExoPlayer.setMediaItem(any()) }
        verify { mockExoPlayer.prepare() }
        assertEquals(testUrl, playerController.playbackState.value.currentUrl)
    }

    @Test
    fun `play calls player play`() {
        playerController.initializePlayer()
        playerController.play()
        
        verify { mockExoPlayer.play() }
    }

    @Test
    fun `pause calls player pause`() {
        playerController.initializePlayer()
        playerController.pause()
        
        verify { mockExoPlayer.pause() }
    }

    @Test
    fun `seekTo calls player seekTo`() {
        playerController.initializePlayer()
        val position = 5000L
        
        playerController.seekTo(position)
        
        verify { mockExoPlayer.seekTo(position) }
    }

    @Test
    fun `seekForward seeks to correct position`() {
        playerController.initializePlayer()
        every { mockExoPlayer.currentPosition } returns 10000L
        every { mockExoPlayer.duration } returns 100000L
        
        playerController.seekForward()
        
        verify { mockExoPlayer.seekTo(20000L) }
    }

    @Test
    fun `seekBackward seeks to correct position`() {
        playerController.initializePlayer()
        every { mockExoPlayer.currentPosition } returns 15000L
        every { mockExoPlayer.duration } returns 100000L
        
        playerController.seekBackward()
        
        verify { mockExoPlayer.seekTo(5000L) }
    }

    @Test
    fun `seekBackward does not seek before zero`() {
        playerController.initializePlayer()
        every { mockExoPlayer.currentPosition } returns 5000L
        every { mockExoPlayer.duration } returns 100000L
        
        playerController.seekBackward()
        
        verify { mockExoPlayer.seekTo(0L) }
    }

    @Test
    fun `getCurrentPosition returns player position`() {
        playerController.initializePlayer()
        val expectedPosition = 12345L
        every { mockExoPlayer.currentPosition } returns expectedPosition
        
        val position = playerController.getCurrentPosition()
        
        assertEquals(expectedPosition, position)
    }

    @Test
    fun `getDuration returns player duration`() {
        playerController.initializePlayer()
        val expectedDuration = 60000L
        every { mockExoPlayer.duration } returns expectedDuration
        
        val duration = playerController.getDuration()
        
        assertEquals(expectedDuration, duration)
    }

    @Test
    fun `isPlaying returns player playing state`() {
        playerController.initializePlayer()
        every { mockExoPlayer.isPlaying } returns true
        
        assertTrue(playerController.isPlaying())
        
        every { mockExoPlayer.isPlaying } returns false
        assertFalse(playerController.isPlaying())
    }

    @Test
    fun `savePlaybackPosition returns current position`() {
        playerController.initializePlayer()
        val expectedPosition = 30000L
        every { mockExoPlayer.currentPosition } returns expectedPosition
        
        val position = playerController.savePlaybackPosition()
        
        assertEquals(expectedPosition, position)
    }

    @Test
    fun `restorePlaybackPosition seeks to saved position`() {
        playerController.initializePlayer()
        val savedPosition = 25000L
        
        playerController.restorePlaybackPosition(savedPosition)
        
        verify { mockExoPlayer.seekTo(savedPosition) }
    }

    @Test
    fun `release removes listener and releases player`() {
        playerController.initializePlayer()
        
        playerController.release()
        
        verify { mockExoPlayer.removeListener(any()) }
        verify { mockExoPlayer.release() }
        assertNull(playerController.getPlayer())
    }

    @Test
    fun `playback state updates on player state changes`() = runTest {
        val listener = slot<Player.Listener>()
        every { mockExoPlayer.addListener(capture(listener)) } just Runs
        
        playerController.initializePlayer()
        
        listener.captured.onPlaybackStateChanged(Player.STATE_BUFFERING)
        assertTrue(playerController.playbackState.value.isLoading)
        
        listener.captured.onPlaybackStateChanged(Player.STATE_ENDED)
        assertTrue(playerController.playbackState.value.isEnded)
    }

    @Test
    fun `playback state updates on error`() = runTest {
        val listener = slot<Player.Listener>()
        every { mockExoPlayer.addListener(capture(listener)) } just Runs
        
        playerController.initializePlayer()
        
        val mockError = mockk<PlaybackException>()
        every { mockError.message } returns "Test error"
        
        listener.captured.onPlayerError(mockError)
        
        assertEquals("Test error", playerController.playbackState.value.error)
    }
}
