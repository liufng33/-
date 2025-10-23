package com.example.videoplayer.ui.url

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayer.data.model.PlaybackOptions
import com.example.videoplayer.data.parser.DirectUrlParser
import com.example.videoplayer.data.parser.JsoupParser
import com.example.videoplayer.data.parser.ParserSource
import com.example.videoplayer.data.parser.VideoParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UrlInputViewModel @Inject constructor(
    private val jsoupParser: JsoupParser,
    private val directUrlParser: DirectUrlParser
) : ViewModel() {

    private val _uiState = MutableStateFlow(UrlInputUiState())
    val uiState: StateFlow<UrlInputUiState> = _uiState.asStateFlow()

    fun updateUrl(url: String) {
        _uiState.value = _uiState.value.copy(url = url)
    }

    fun selectParserSource(source: ParserSource) {
        _uiState.value = _uiState.value.copy(selectedParser = source)
    }

    fun parseUrl() {
        val url = _uiState.value.url.trim()
        if (url.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                playbackOptions = PlaybackOptions.Error("Please enter a valid URL")
            )
            return
        }

        if (!isValidUrl(url)) {
            _uiState.value = _uiState.value.copy(
                playbackOptions = PlaybackOptions.Error("Invalid URL format")
            )
            return
        }

        _uiState.value = _uiState.value.copy(playbackOptions = PlaybackOptions.Loading)

        viewModelScope.launch {
            try {
                val parser = getSelectedParser()
                val result = parser.parse(url)
                _uiState.value = _uiState.value.copy(playbackOptions = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    playbackOptions = PlaybackOptions.Error("Error: ${e.message}")
                )
            }
        }
    }

    fun clearResults() {
        _uiState.value = _uiState.value.copy(playbackOptions = PlaybackOptions.Idle)
    }

    private fun getSelectedParser(): VideoParser {
        return when (_uiState.value.selectedParser) {
            ParserSource.JSOUP -> jsoupParser
            ParserSource.DIRECT -> directUrlParser
            ParserSource.REGEX -> jsoupParser // Fallback to jsoup
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            val urlPattern = "^(https?://)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
            urlPattern.matches(url)
        } catch (e: Exception) {
            false
        }
    }
}

data class UrlInputUiState(
    val url: String = "",
    val selectedParser: ParserSource = ParserSource.JSOUP,
    val playbackOptions: PlaybackOptions = PlaybackOptions.Idle
)
