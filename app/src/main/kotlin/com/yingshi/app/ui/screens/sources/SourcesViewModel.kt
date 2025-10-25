package com.yingshi.app.ui.screens.sources

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class VideoSource(
    val id: String,
    val name: String,
    val url: String
)

data class SourcesUiState(
    val sources: List<VideoSource> = emptyList(),
    val isAddingSource: Boolean = false
)

@HiltViewModel
class SourcesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(
        SourcesUiState(
            sources = listOf(
                VideoSource("1", "默认API", "https://132130.v.nxog.top/api1.php?id=3"),
                VideoSource("2", "示例源1", "https://example.com/api"),
                VideoSource("3", "示例源2", "https://example.com/api2")
            )
        )
    )
    val uiState: StateFlow<SourcesUiState> = _uiState.asStateFlow()

    fun showAddSourceDialog() {
        _uiState.value = _uiState.value.copy(isAddingSource = true)
    }

    fun hideAddSourceDialog() {
        _uiState.value = _uiState.value.copy(isAddingSource = false)
    }

    fun addSource(name: String, url: String) {
        val newSource = VideoSource(
            id = System.currentTimeMillis().toString(),
            name = name,
            url = url
        )
        _uiState.value = _uiState.value.copy(
            sources = _uiState.value.sources + newSource,
            isAddingSource = false
        )
    }

    fun deleteSource(sourceId: String) {
        _uiState.value = _uiState.value.copy(
            sources = _uiState.value.sources.filter { it.id != sourceId }
        )
    }
}
