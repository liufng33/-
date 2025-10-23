package com.cleanarch.app.ui.screens.home

import androidx.lifecycle.viewModelScope
import com.cleanarch.app.ui.base.BaseViewModel
import com.cleanarch.app.ui.base.UiEffect
import com.cleanarch.app.ui.base.UiEvent
import com.cleanarch.app.ui.base.UiState
import com.cleanarch.domain.usecase.GetSampleDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSampleDataUseCase: GetSampleDataUseCase
) : BaseViewModel<HomeViewModel.State, HomeViewModel.Event, HomeViewModel.Effect>() {
    
    data class State(
        val isLoading: Boolean = false,
        val data: String = "No data loaded"
    ) : UiState
    
    sealed class Event : UiEvent {
        object LoadData : Event()
    }
    
    sealed class Effect : UiEffect {
        data class ShowError(val message: String) : Effect()
    }
    
    override fun createInitialState(): State = State()
    
    override fun handleEvent(event: Event) {
        when (event) {
            is Event.LoadData -> loadData()
        }
    }
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            
            getSampleDataUseCase().onSuccess { data ->
                setState { copy(isLoading = false, data = data) }
            }.onError { _, message ->
                setState { copy(isLoading = false) }
                setEffect { Effect.ShowError(message ?: "Unknown error") }
            }
        }
    }
}
