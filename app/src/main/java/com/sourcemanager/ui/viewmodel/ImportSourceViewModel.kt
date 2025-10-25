package com.sourcemanager.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sourcemanager.domain.model.ImportResult
import com.sourcemanager.domain.usecase.ImportSourcesFromApiUseCase
import com.sourcemanager.domain.usecase.ImportSourcesFromJsonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ImportSourceUiState(
    val apiUrl: String = "",
    val jsonContent: String = "",
    val isLoading: Boolean = false,
    val progress: Float = 0f,
    val progressMessage: String = "",
    val error: String? = null,
    val successMessage: String? = null,
    val importedCount: Int = 0,
    val apiUrlError: String? = null
)

@HiltViewModel
class ImportSourceViewModel @Inject constructor(
    private val importFromApiUseCase: ImportSourcesFromApiUseCase,
    private val importFromJsonUseCase: ImportSourcesFromJsonUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportSourceUiState())
    val uiState: StateFlow<ImportSourceUiState> = _uiState.asStateFlow()

    init {
        restoreState()
    }

    private fun restoreState() {
        _uiState.value = ImportSourceUiState(
            apiUrl = savedStateHandle["apiUrl"] ?: "",
            jsonContent = savedStateHandle["jsonContent"] ?: ""
        )
    }

    fun updateApiUrl(url: String) {
        _uiState.value = _uiState.value.copy(
            apiUrl = url,
            apiUrlError = null
        )
        savedStateHandle["apiUrl"] = url
    }

    fun updateJsonContent(content: String) {
        _uiState.value = _uiState.value.copy(jsonContent = content)
        savedStateHandle["jsonContent"] = content
    }

    fun importFromApi() {
        if (_uiState.value.apiUrl.isBlank()) {
            _uiState.value = _uiState.value.copy(
                apiUrlError = "API URL is required"
            )
            return
        }

        if (!android.util.Patterns.WEB_URL.matcher(_uiState.value.apiUrl).matches()) {
            _uiState.value = _uiState.value.copy(
                apiUrlError = "Invalid URL format"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                successMessage = null,
                progress = 0f
            )

            importFromApiUseCase(_uiState.value.apiUrl).collect { result ->
                when (result) {
                    is ImportResult.Progress -> {
                        val progress = if (result.total > 0) {
                            result.current.toFloat() / result.total.toFloat()
                        } else 0f
                        _uiState.value = _uiState.value.copy(
                            progress = progress,
                            progressMessage = "Importing ${result.current} of ${result.total}..."
                        )
                    }
                    is ImportResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            progress = 1f,
                            successMessage = "Successfully imported ${result.importedCount} sources",
                            importedCount = result.importedCount
                        )
                        clearSavedState()
                    }
                    is ImportResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message,
                            progress = 0f
                        )
                    }
                }
            }
        }
    }

    fun importFromJson() {
        if (_uiState.value.jsonContent.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "JSON content is required"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                successMessage = null,
                progress = 0f
            )

            importFromJsonUseCase(_uiState.value.jsonContent).collect { result ->
                when (result) {
                    is ImportResult.Progress -> {
                        val progress = if (result.total > 0) {
                            result.current.toFloat() / result.total.toFloat()
                        } else 0f
                        _uiState.value = _uiState.value.copy(
                            progress = progress,
                            progressMessage = "Importing ${result.current} of ${result.total}..."
                        )
                    }
                    is ImportResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            progress = 1f,
                            successMessage = "Successfully imported ${result.importedCount} sources",
                            importedCount = result.importedCount
                        )
                        clearSavedState()
                    }
                    is ImportResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message,
                            progress = 0f
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    private fun clearSavedState() {
        savedStateHandle.remove<String>("apiUrl")
        savedStateHandle.remove<String>("jsonContent")
    }
}
