package com.sourcemanager.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sourcemanager.domain.model.Source
import com.sourcemanager.domain.model.SourceType
import com.sourcemanager.domain.usecase.AddSourceUseCase
import com.sourcemanager.domain.usecase.GetSourceByIdUseCase
import com.sourcemanager.domain.usecase.UpdateSourceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AddEditSourceUiState(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val url: String = "",
    val description: String = "",
    val sourceType: SourceType = SourceType.SEARCH,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val nameError: String? = null,
    val urlError: String? = null
)

@HiltViewModel
class AddEditSourceViewModel @Inject constructor(
    private val addSourceUseCase: AddSourceUseCase,
    private val updateSourceUseCase: UpdateSourceUseCase,
    private val getSourceByIdUseCase: GetSourceByIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditSourceUiState())
    val uiState: StateFlow<AddEditSourceUiState> = _uiState.asStateFlow()

    init {
        restoreState()
    }

    private fun restoreState() {
        _uiState.value = AddEditSourceUiState(
            id = savedStateHandle["id"] ?: UUID.randomUUID().toString(),
            name = savedStateHandle["name"] ?: "",
            url = savedStateHandle["url"] ?: "",
            description = savedStateHandle["description"] ?: "",
            sourceType = savedStateHandle.get<String>("sourceType")?.let { 
                SourceType.valueOf(it) 
            } ?: SourceType.SEARCH
        )
    }

    fun loadSource(source: Source) {
        _uiState.value = _uiState.value.copy(
            id = source.id,
            name = source.name,
            url = source.url,
            description = source.description,
            sourceType = source.type
        )
        saveState()
    }

    fun loadSourceById(sourceId: String) {
        viewModelScope.launch {
            getSourceByIdUseCase(sourceId)?.let { source ->
                loadSource(source)
            }
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            nameError = null
        )
        savedStateHandle["name"] = name
    }

    fun updateUrl(url: String) {
        _uiState.value = _uiState.value.copy(
            url = url,
            urlError = null
        )
        savedStateHandle["url"] = url
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
        savedStateHandle["description"] = description
    }

    fun updateSourceType(type: SourceType) {
        _uiState.value = _uiState.value.copy(sourceType = type)
        savedStateHandle["sourceType"] = type.name
    }

    fun saveSource(isEdit: Boolean = false) {
        if (!validateInputs()) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val source = Source(
                id = _uiState.value.id,
                name = _uiState.value.name.trim(),
                url = _uiState.value.url.trim(),
                description = _uiState.value.description.trim(),
                type = _uiState.value.sourceType,
                isActive = false
            )

            val result = if (isEdit) {
                updateSourceUseCase(source)
            } else {
                addSourceUseCase(source)
            }

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSaved = true
                    )
                    clearSavedState()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to save source"
                    )
                }
            )
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        var nameError: String? = null
        var urlError: String? = null

        if (_uiState.value.name.isBlank()) {
            nameError = "Name is required"
            isValid = false
        }

        if (_uiState.value.url.isBlank()) {
            urlError = "URL is required"
            isValid = false
        } else if (!android.util.Patterns.WEB_URL.matcher(_uiState.value.url).matches()) {
            urlError = "Invalid URL format"
            isValid = false
        }

        _uiState.value = _uiState.value.copy(
            nameError = nameError,
            urlError = urlError
        )

        return isValid
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun saveState() {
        savedStateHandle["id"] = _uiState.value.id
        savedStateHandle["name"] = _uiState.value.name
        savedStateHandle["url"] = _uiState.value.url
        savedStateHandle["description"] = _uiState.value.description
        savedStateHandle["sourceType"] = _uiState.value.sourceType.name
    }

    private fun clearSavedState() {
        savedStateHandle.remove<String>("id")
        savedStateHandle.remove<String>("name")
        savedStateHandle.remove<String>("url")
        savedStateHandle.remove<String>("description")
        savedStateHandle.remove<String>("sourceType")
    }
}
