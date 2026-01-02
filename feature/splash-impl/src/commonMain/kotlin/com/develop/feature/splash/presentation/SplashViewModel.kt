package com.develop.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develop.feature.splash.domain.usecase.SplashUseCases
import com.develop.feature.splash.presentation.contract.SplashEffect
import com.develop.feature.splash.presentation.contract.SplashState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val useCases: SplashUseCases
) : ViewModel() {
    
    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()
    
    private val _effect = Channel<SplashEffect>(capacity = Channel.BUFFERED)
    val effect: Flow<SplashEffect> = _effect.receiveAsFlow()
    
    init {
        authenticate()
    }
    
    private fun authenticate() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            useCases.authenticate.execute()
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(SplashEffect.NavigateToMain)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _effect.send(SplashEffect.ShowError(error.message ?: "Authentication failed"))
                }
        }
    }
}
