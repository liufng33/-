package com.example.videoplayer.ui.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.cast.CastPlayer
import androidx.media3.cast.SessionAvailabilityListener
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null
    private var currentPlayer: Player? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            _playbackState.value = _playbackState.value.copy(
                isPlaying = currentPlayer?.isPlaying ?: false,
                isLoading = state == Player.STATE_BUFFERING,
                isEnded = state == Player.STATE_ENDED
            )
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            _playbackState.value = _playbackState.value.copy(
                error = error.message ?: "Playback error occurred"
            )
        }
    }

    fun initializePlayer(): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context)
                .build()
                .apply {
                    addListener(playerListener)
                }
            currentPlayer = exoPlayer
        }
        return exoPlayer!!
    }

    fun prepare(videoUrl: String) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        currentPlayer?.apply {
            setMediaItem(mediaItem)
            prepare()
        }
        _playbackState.value = _playbackState.value.copy(
            currentUrl = videoUrl,
            error = null
        )
    }

    fun play() {
        currentPlayer?.play()
    }

    fun pause() {
        currentPlayer?.pause()
    }

    fun seekTo(positionMs: Long) {
        currentPlayer?.seekTo(positionMs)
    }

    fun seekForward(incrementMs: Long = 10000) {
        currentPlayer?.let { player ->
            val newPosition = (player.currentPosition + incrementMs).coerceAtMost(player.duration)
            player.seekTo(newPosition)
        }
    }

    fun seekBackward(decrementMs: Long = 10000) {
        currentPlayer?.let { player ->
            val newPosition = (player.currentPosition - decrementMs).coerceAtLeast(0)
            player.seekTo(newPosition)
        }
    }

    fun getCurrentPosition(): Long = currentPlayer?.currentPosition ?: 0

    fun getDuration(): Long = currentPlayer?.duration ?: 0

    fun isPlaying(): Boolean = currentPlayer?.isPlaying ?: false

    fun savePlaybackPosition(): Long = getCurrentPosition()

    fun restorePlaybackPosition(position: Long) {
        currentPlayer?.seekTo(position)
    }

    fun release() {
        currentPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null
        currentPlayer = null
        _playbackState.value = PlaybackState()
    }

    fun getPlayer(): Player? = currentPlayer
}

data class PlaybackState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val isEnded: Boolean = false,
    val currentUrl: String? = null,
    val error: String? = null,
    val currentPosition: Long = 0,
    val duration: Long = 0
)
