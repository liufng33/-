package com.example.videoplayer.ui.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.ui.PlayerView
import kotlin.math.roundToInt

@Composable
fun PlayerScreen(
    videoUrl: String,
    videoTitle: String,
    onBackPressed: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(videoUrl, videoTitle) {
        viewModel.initializePlayer(videoUrl, videoTitle)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.pause()
                    viewModel.savePlaybackState()
                }
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.restorePlaybackState()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.releasePlayer()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VideoPlayerView(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }

        if (uiState.showControls) {
            PlayerControls(
                uiState = uiState,
                onPlayPause = { viewModel.togglePlayPause() },
                onSeekBackward = { viewModel.seekBackward() },
                onSeekForward = { viewModel.seekForward() },
                onSeekTo = { viewModel.seekTo(it) },
                onBack = onBackPressed,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    viewModel.toggleControls()
                }
        )

        uiState.error?.let { error ->
            ErrorOverlay(
                error = error,
                onDismiss = onBackPressed,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun VideoPlayerView(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            PlayerView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                useController = false
                player = viewModel.getPlayer()
            }
        },
        modifier = modifier
    )
}

@Composable
fun PlayerControls(
    uiState: PlayerUiState,
    onPlayPause: () -> Unit,
    onSeekBackward: () -> Unit,
    onSeekForward: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TopControlBar(
                title = uiState.videoTitle,
                onBack = onBack
            )

            Spacer(modifier = Modifier.weight(1f))

            CenterControls(
                isPlaying = uiState.isPlaying,
                onPlayPause = onPlayPause,
                onSeekBackward = onSeekBackward,
                onSeekForward = onSeekForward,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.weight(1f))

            BottomControlBar(
                currentPosition = uiState.currentPosition,
                duration = uiState.duration,
                onSeekTo = onSeekTo
            )
        }
    }
}

@Composable
fun TopControlBar(
    title: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun CenterControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onSeekBackward: () -> Unit,
    onSeekForward: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onSeekBackward,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Replay10,
                contentDescription = "Seek backward",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        IconButton(
            onClick = onPlayPause,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }

        IconButton(
            onClick = onSeekForward,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Forward10,
                contentDescription = "Seek forward",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun BottomControlBar(
    currentPosition: Long,
    duration: Long,
    onSeekTo: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        var sliderPosition by remember { mutableFloatStateOf(0f) }
        var isUserSeeking by remember { mutableStateOf(false) }

        LaunchedEffect(currentPosition, isUserSeeking) {
            if (!isUserSeeking && duration > 0) {
                sliderPosition = (currentPosition.toFloat() / duration.toFloat())
            }
        }

        Slider(
            value = sliderPosition,
            onValueChange = {
                isUserSeeking = true
                sliderPosition = it
            },
            onValueChangeFinished = {
                isUserSeeking = false
                val seekPosition = (sliderPosition * duration).toLong()
                onSeekTo(seekPosition)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.Gray
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = formatTime(duration),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ErrorOverlay(
    error: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Playback Error",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go Back")
            }
        }
    }
}

fun formatTime(milliseconds: Long): String {
    if (milliseconds < 0) return "00:00"
    
    val totalSeconds = (milliseconds / 1000).toInt()
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
