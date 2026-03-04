package com.on.turip.ui.compose.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.domain.session.SessionState
import com.on.turip.domain.session.usecase.DetermineInitialSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val determineInitialSessionUseCase: DetermineInitialSessionUseCase,
) : ViewModel() {
    private val _uiEffect: Channel<SplashUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<SplashUiEffect> = _uiEffect.receiveAsFlow()

    fun determineStartDestination() {
        viewModelScope.launch {
            val sessionState: SessionState = determineInitialSessionUseCase()
            when (sessionState) {
                is SessionState.Member -> _uiEffect.send(SplashUiEffect.NavigateToMain)
                SessionState.Guest, SessionState.Uninitialized -> _uiEffect.send(SplashUiEffect.NavigateToLogin)
            }
        }
    }
}
