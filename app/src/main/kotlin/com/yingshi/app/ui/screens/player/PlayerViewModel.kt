package com.yingshi.app.ui.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class PlayerUiState(
    val videoUrl: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        val videoUrl = savedStateHandle.get<String>("videoUrl") ?: ""
        _uiState.value = PlayerUiState(videoUrl = videoUrl)
    }

    fun onPlayerReady() {
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    fun onPlayerError(error: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = error
        )
    }
}
