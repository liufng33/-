package com.cleanarch.app.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : UiState, Event : UiEvent, Effect : UiEffect> : ViewModel() {
    
    private val initialState: State by lazy { createInitialState() }
    abstract fun createInitialState(): State
    
    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()
    
    private val _event: Channel<Event> = Channel()
    
    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()
    
    val currentState: State
        get() = uiState.value
    
    init {
        subscribeEvents()
    }
    
    private fun subscribeEvents() {
        viewModelScope.launch {
            _event.receiveAsFlow().collect { event ->
                handleEvent(event)
            }
        }
    }
    
    abstract fun handleEvent(event: Event)
    
    fun setEvent(event: Event) {
        viewModelScope.launch { _event.send(event) }
    }
    
    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _uiState.value = newState
    }
    
    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }
}

interface UiState

interface UiEvent

interface UiEffect
