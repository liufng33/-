package com.cleanarch.app.ui.screens.player

import com.cleanarch.app.ui.base.BaseViewModel
import com.cleanarch.app.ui.base.UiEffect
import com.cleanarch.app.ui.base.UiEvent
import com.cleanarch.app.ui.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor() :
    BaseViewModel<PlayerViewModel.State, PlayerViewModel.Event, PlayerViewModel.Effect>() {
    
    data class State(
        val isPlaying: Boolean = false,
        val currentPosition: Long = 0L
    ) : UiState
    
    sealed class Event : UiEvent {
        object Play : Event()
        object Pause : Event()
        data class SeekTo(val position: Long) : Event()
    }
    
    sealed class Effect : UiEffect {
        data class ShowError(val message: String) : Effect()
    }
    
    override fun createInitialState(): State = State()
    
    override fun handleEvent(event: Event) {
        when (event) {
            is Event.Play -> setState { copy(isPlaying = true) }
            is Event.Pause -> setState { copy(isPlaying = false) }
            is Event.SeekTo -> setState { copy(currentPosition = event.position) }
        }
    }
}
