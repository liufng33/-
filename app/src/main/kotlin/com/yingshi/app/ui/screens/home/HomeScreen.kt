package com.yingshi.app.ui.screens.home

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPlayer: (String) -> Unit,
    onNavigateToSources: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("影视播放器") },
                actions = {
                    IconButton(onClick = onNavigateToSources) {
                        Icon(Icons.Default.Settings, contentDescription = "视频源管理")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎬",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "在线视频播放器",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "粘贴视频链接即可播放",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // URL Input
            OutlinedTextField(
                value = uiState.videoUrl,
                onValueChange = { viewModel.updateVideoUrl(it) },
                label = { Text("视频URL") },
                placeholder = { Text("粘贴视频链接...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3,
                maxLines = 5,
                isError = uiState.error != null
            )

            // Parser Selection
            var expanded by remember { mutableStateOf(false) }
            val parsers = listOf("默认解析器", "解析器1", "解析器2", "解析器3")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.selectedParser,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("选择解析器") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    parsers.forEach { parser ->
                        DropdownMenuItem(
                            text = { Text(parser) },
                            onClick = {
                                viewModel.updateSelectedParser(parser)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Error Message
            if (uiState.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Parse and Play Button
            Button(
                onClick = {
                    viewModel.parseAndPlay { parsedUrl ->
                        val encodedUrl = Uri.encode(parsedUrl)
                        onNavigateToPlayer(encodedUrl)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("解析并播放", style = MaterialTheme.typography.titleMedium)
                }
            }

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "使用说明",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "1. 粘贴视频链接到输入框",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "2. 选择合适的解析器",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "3. 点击\"解析并播放\"按钮",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "4. 等待加载完成后即可观看",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
