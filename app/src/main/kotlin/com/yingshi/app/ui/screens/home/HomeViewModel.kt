package com.yingshi.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remotedata.data.remote.datasource.ParserRemoteDataSource
import com.remotedata.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val videoUrl: String = "",
    val selectedParser: String = "默认解析器",
    val isLoading: Boolean = false,
    val error: String? = null,
    val parsedUrl: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val parserDataSource: ParserRemoteDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun updateVideoUrl(url: String) {
        _uiState.value = _uiState.value.copy(videoUrl = url, error = null)
    }

    fun updateSelectedParser(parser: String) {
        _uiState.value = _uiState.value.copy(selectedParser = parser)
    }

    fun parseAndPlay(onSuccess: (String) -> Unit) {
        val url = _uiState.value.videoUrl
        if (url.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "请输入视频URL")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // For now, use the URL directly since we don't have a specific parser API
            // In a real app, you would call the parser service here
            try {
                // Simulate parsing - in real implementation, call parserDataSource
                val parsedUrl = url // Direct playback for now
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    parsedUrl = parsedUrl
                )
                onSuccess(parsedUrl)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "解析失败: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
