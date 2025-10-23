package com.sourcemanager.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.model.SourceType
import com.sourcemanager.domain.usecase.DeleteSourceUseCase
import com.sourcemanager.domain.usecase.GetSourcesUseCase
import com.sourcemanager.domain.usecase.SwitchActiveSourceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SourceListUiState(
    val sources: List<Source> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class SourceListViewModel(
    private val getSourcesUseCase: GetSourcesUseCase,
    private val deleteSourceUseCase: DeleteSourceUseCase,
    private val switchActiveSourceUseCase: SwitchActiveSourceUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SourceListUiState())
    val uiState: StateFlow<SourceListUiState> = _uiState.asStateFlow()

    init {
        loadSources()
        restoreState()
    }

    private fun restoreState() {
        savedStateHandle.get<String>("error")?.let { error ->
            _uiState.value = _uiState.value.copy(error = error)
        }
    }

    private fun loadSources() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getSourcesUseCase().collect { sources ->
                _uiState.value = _uiState.value.copy(
                    sources = sources,
                    isLoading = false
                )
            }
        }
    }

    fun deleteSource(id: String) {
        viewModelScope.launch {
            deleteSourceUseCase(id).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Source deleted successfully"
                    )
                },
                onFailure = { error ->
                    val errorMessage = error.message ?: "Failed to delete source"
                    _uiState.value = _uiState.value.copy(error = errorMessage)
                    savedStateHandle["error"] = errorMessage
                }
            )
        }
    }

    fun switchActiveSource(id: String, type: SourceType) {
        viewModelScope.launch {
            switchActiveSourceUseCase(id, type).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Active source switched successfully"
                    )
                },
                onFailure = { error ->
                    val errorMessage = error.message ?: "Failed to switch source"
                    _uiState.value = _uiState.value.copy(error = errorMessage)
                    savedStateHandle["error"] = errorMessage
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
        savedStateHandle.remove<String>("error")
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}
