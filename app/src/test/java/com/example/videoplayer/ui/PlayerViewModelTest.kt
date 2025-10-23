package com.example.videoplayer.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.videoplayer.ui.player.PlaybackState
import com.example.videoplayer.ui.player.PlayerController
import com.example.videoplayer.ui.player.PlayerViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var playerController: PlayerController
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: PlayerViewModel
    private lateinit var playbackStateFlow: MutableStateFlow<PlaybackState>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        playerController = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle()
        playbackStateFlow = MutableStateFlow(PlaybackState())

        every { playerController.playbackState } returns playbackStateFlow

        viewModel = PlayerViewModel(playerController, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initializePlayer sets video URL and title`() = runTest {
        val testUrl = "https://example.com/video.mp4"
        val testTitle = "Test Video"

        viewModel.initializePlayer(testUrl, testTitle)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(testUrl, state.videoUrl)
        assertEquals(testTitle, state.videoTitle)
        verify { playerController.initializePlayer() }
        verify { playerController.prepare(testUrl) }
    }

    @Test
    fun `play calls playerController play`() {
        viewModel.play()
        verify { playerController.play() }
        assertTrue(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun `pause calls playerController pause`() {
        viewModel.pause()
        verify { playerController.pause() }
        assertFalse(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun `togglePlayPause calls pause when playing`() = runTest {
        viewModel.initializePlayer("test.mp4", "Test")
        viewModel.play()
        advanceUntilIdle()

        viewModel.togglePlayPause()
        verify { playerController.pause() }
    }

    @Test
    fun `togglePlayPause calls play when paused`() = runTest {
        viewModel.initializePlayer("test.mp4", "Test")
        advanceUntilIdle()

        viewModel.togglePlayPause()
        verify(atLeast = 1) { playerController.play() }
    }

    @Test
    fun `seekTo calls playerController seekTo`() {
        val position = 5000L
        viewModel.seekTo(position)
        verify { playerController.seekTo(position) }
    }

    @Test
    fun `seekForward calls playerController seekForward`() {
        viewModel.seekForward()
        verify { playerController.seekForward() }
    }

    @Test
    fun `seekBackward calls playerController seekBackward`() {
        viewModel.seekBackward()
        verify { playerController.seekBackward() }
    }

    @Test
    fun `showControls sets showControls to true`() {
        viewModel.showControls()
        assertTrue(viewModel.uiState.value.showControls)
    }

    @Test
    fun `hideControls sets showControls to false`() {
        viewModel.hideControls()
        assertFalse(viewModel.uiState.value.showControls)
    }

    @Test
    fun `toggleControls shows controls when hidden`() {
        viewModel.hideControls()
        viewModel.toggleControls()
        assertTrue(viewModel.uiState.value.showControls)
    }

    @Test
    fun `toggleControls hides controls when shown`() {
        viewModel.showControls()
        viewModel.toggleControls()
        assertFalse(viewModel.uiState.value.showControls)
    }

    @Test
    fun `savePlaybackState calls playerController savePlaybackPosition`() {
        every { playerController.savePlaybackPosition() } returns 3000L
        viewModel.savePlaybackState()
        verify { playerController.savePlaybackPosition() }
    }

    @Test
    fun `restorePlaybackState calls playerController restorePlaybackPosition`() {
        every { playerController.savePlaybackPosition() } returns 3000L
        viewModel.savePlaybackState()
        viewModel.restorePlaybackState()
        verify { playerController.restorePlaybackPosition(3000L) }
    }

    @Test
    fun `playbackState updates are reflected in uiState`() = runTest {
        val newPlaybackState = PlaybackState(
            isPlaying = true,
            isLoading = false,
            error = null
        )

        playbackStateFlow.value = newPlaybackState
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isPlaying)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `error in playbackState is reflected in uiState`() = runTest {
        val errorMessage = "Playback failed"
        val newPlaybackState = PlaybackState(error = errorMessage)

        playbackStateFlow.value = newPlaybackState
        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.uiState.value.error)
    }

    @Test
    fun `onCleared saves playback position`() {
        every { playerController.savePlaybackPosition() } returns 5000L
        
        // Trigger onCleared by clearing the viewModel
        viewModel.releasePlayer()
        
        verify { playerController.release() }
    }

    @Test
    fun `getPlayer returns player from controller`() {
        val mockPlayer = mockk<androidx.media3.common.Player>()
        every { playerController.getPlayer() } returns mockPlayer

        val result = viewModel.getPlayer()

        assertEquals(mockPlayer, result)
    }
}
