package com.cleanarch.app.ui.screens.settings

import com.cleanarch.app.ui.base.BaseViewModel
import com.cleanarch.app.ui.base.UiEffect
import com.cleanarch.app.ui.base.UiEvent
import com.cleanarch.app.ui.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() :
    BaseViewModel<SettingsViewModel.State, SettingsViewModel.Event, SettingsViewModel.Effect>() {
    
    data class State(
        val isDarkMode: Boolean = false,
        val notificationsEnabled: Boolean = true
    ) : UiState
    
    sealed class Event : UiEvent {
        object ToggleDarkMode : Event()
        object ToggleNotifications : Event()
    }
    
    sealed class Effect : UiEffect {
        data class ShowToast(val message: String) : Effect()
    }
    
    override fun createInitialState(): State = State()
    
    override fun handleEvent(event: Event) {
        when (event) {
            is Event.ToggleDarkMode -> setState { copy(isDarkMode = !isDarkMode) }
            is Event.ToggleNotifications -> setState { copy(notificationsEnabled = !notificationsEnabled) }
        }
    }
}
