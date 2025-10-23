package com.cleanarch.app.ui.screens.search

import com.cleanarch.app.ui.base.BaseViewModel
import com.cleanarch.app.ui.base.UiEffect
import com.cleanarch.app.ui.base.UiEvent
import com.cleanarch.app.ui.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() :
    BaseViewModel<SearchViewModel.State, SearchViewModel.Event, SearchViewModel.Effect>() {
    
    data class State(
        val searchQuery: String = "",
        val isLoading: Boolean = false
    ) : UiState
    
    sealed class Event : UiEvent {
        data class OnSearchQueryChanged(val query: String) : Event()
    }
    
    sealed class Effect : UiEffect {
        data class ShowError(val message: String) : Effect()
    }
    
    override fun createInitialState(): State = State()
    
    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
            }
        }
    }
}
