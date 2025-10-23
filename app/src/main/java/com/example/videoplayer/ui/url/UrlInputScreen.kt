package com.example.videoplayer.ui.url

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.videoplayer.data.model.PlaybackOptions
import com.example.videoplayer.data.model.StreamQuality
import com.example.videoplayer.data.parser.ParserSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlInputScreen(
    onNavigateToPlayer: (String, String) -> Unit,
    viewModel: UrlInputViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Player") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Enter Video URL",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = uiState.url,
                onValueChange = { viewModel.updateUrl(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Video URL") },
                placeholder = { Text("https://example.com/video.mp4") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            clipboardManager.getText()?.text?.let { text ->
                                viewModel.updateUrl(text)
                            }
                        }
                    ) {
                        Icon(Icons.Default.ContentPaste, "Paste from clipboard")
                    }
                },
                singleLine = true
            )

            ParserSourceSelector(
                selectedSource = uiState.selectedParser,
                onSourceSelected = { viewModel.selectParserSource(it) }
            )

            Button(
                onClick = { viewModel.parseUrl() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.url.isNotEmpty() && uiState.playbackOptions !is PlaybackOptions.Loading
            ) {
                Text("Parse URL")
            }

            when (val options = uiState.playbackOptions) {
                is PlaybackOptions.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is PlaybackOptions.Success -> {
                    PlaybackOptionsView(
                        options = options,
                        onQualitySelected = { quality ->
                            onNavigateToPlayer(quality.url, options.title)
                        }
                    )
                }

                is PlaybackOptions.Error -> {
                    ErrorView(
                        error = options,
                        onRetry = { viewModel.parseUrl() },
                        onTryFallback = { fallbackUrl ->
                            onNavigateToPlayer(fallbackUrl, "Video")
                        }
                    )
                }

                PlaybackOptions.Idle -> {
                    Text(
                        text = "Paste a video URL and select a parser to extract playback options",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParserSourceSelector(
    selectedSource: ParserSource,
    onSourceSelected: (ParserSource) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedSource.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Parser Source") },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, "Dropdown")
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ParserSource.entries.forEach { source ->
                DropdownMenuItem(
                    text = { Text(source.displayName) },
                    onClick = {
                        onSourceSelected(source)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PlaybackOptionsView(
    options: PlaybackOptions.Success,
    onQualitySelected: (StreamQuality) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = options.title,
                style = MaterialTheme.typography.titleMedium
            )

            if (options.qualities.isNotEmpty()) {
                Text(
                    text = "Available Qualities:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(options.qualities) { quality ->
                        QualityItem(
                            quality = quality,
                            onClick = { onQualitySelected(quality) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QualityItem(
    quality: StreamQuality,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quality.quality,
                    style = MaterialTheme.typography.titleSmall
                )
                quality.format?.let {
                    Text(
                        text = "Format: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(Icons.Default.PlayArrow, "Play")
        }
    }
}

@Composable
fun ErrorView(
    error: PlaybackOptions.Error,
    onRetry: () -> Unit,
    onTryFallback: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Text(
                text = error.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRetry,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Retry")
                }

                error.fallbackUrl?.let { fallbackUrl ->
                    Button(
                        onClick = { onTryFallback(fallbackUrl) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Try Fallback")
                    }
                }
            }
        }
    }
}
