package com.example.videoplayer.ui.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlayerController,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var progressUpdateJob: Job? = null
    private var savedPosition: Long = 0

    init {
        savedStateHandle.get<String>("videoUrl")?.let { url ->
            savedStateHandle.get<String>("videoTitle")?.let { title ->
                initializePlayer(url, title)
            }
        }

        viewModelScope.launch {
            playerController.playbackState.collect { playbackState ->
                _uiState.value = _uiState.value.copy(
                    isPlaying = playbackState.isPlaying,
                    isLoading = playbackState.isLoading,
                    error = playbackState.error
                )
            }
        }
    }

    fun initializePlayer(videoUrl: String, title: String) {
        _uiState.value = _uiState.value.copy(
            videoUrl = videoUrl,
            videoTitle = title,
            isLoading = true
        )

        playerController.initializePlayer()
        playerController.prepare(videoUrl)
        
        startProgressUpdates()
    }

    fun play() {
        playerController.play()
        _uiState.value = _uiState.value.copy(isPlaying = true)
    }

    fun pause() {
        playerController.pause()
        _uiState.value = _uiState.value.copy(isPlaying = false)
    }

    fun togglePlayPause() {
        if (_uiState.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun seekTo(positionMs: Long) {
        playerController.seekTo(positionMs)
        updateProgress()
    }

    fun seekForward() {
        playerController.seekForward()
        updateProgress()
    }

    fun seekBackward() {
        playerController.seekBackward()
        updateProgress()
    }

    fun showControls() {
        _uiState.value = _uiState.value.copy(showControls = true)
        scheduleHideControls()
    }

    fun hideControls() {
        _uiState.value = _uiState.value.copy(showControls = false)
    }

    fun toggleControls() {
        if (_uiState.value.showControls) {
            hideControls()
        } else {
            showControls()
        }
    }

    fun onUserInteraction() {
        showControls()
    }

    fun savePlaybackState() {
        savedPosition = playerController.savePlaybackPosition()
    }

    fun restorePlaybackState() {
        if (savedPosition > 0) {
            playerController.restorePlaybackPosition(savedPosition)
        }
    }

    fun getPlayer() = playerController.getPlayer()

    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (isActive) {
                updateProgress()
                delay(1000)
            }
        }
    }

    private fun updateProgress() {
        val currentPosition = playerController.getCurrentPosition()
        val duration = playerController.getDuration()
        _uiState.value = _uiState.value.copy(
            currentPosition = currentPosition,
            duration = duration
        )
    }

    private fun scheduleHideControls() {
        viewModelScope.launch {
            delay(3000)
            if (_uiState.value.isPlaying) {
                hideControls()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        progressUpdateJob?.cancel()
        savedPosition = playerController.savePlaybackPosition()
    }

    fun releasePlayer() {
        progressUpdateJob?.cancel()
        playerController.release()
    }
}

data class PlayerUiState(
    val videoUrl: String = "",
    val videoTitle: String = "",
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val showControls: Boolean = true,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val error: String? = null
)
