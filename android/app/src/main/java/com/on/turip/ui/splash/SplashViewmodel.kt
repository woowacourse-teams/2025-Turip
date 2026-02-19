package com.on.turip.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.domain.login.usecase.CheckUserSignedInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewmodel @Inject constructor(
    private val checkUserSignedInUseCase: CheckUserSignedInUseCase,
) : ViewModel() {
    private val _uiEffect = Channel<SplashEffect>(Channel.BUFFERED)
    val uiEffect: Flow<SplashEffect> = _uiEffect.receiveAsFlow()

    fun checkAutoLogin() {
        viewModelScope.launch {
            checkUserSignedInUseCase()
            _uiEffect.send(SplashEffect.NavigateMain)
        }
    }
}
