package com.on.turip.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.domain.login.usecase.CheckUserSignedInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewmodel @Inject constructor(
    private val checkUserSignedInUseCase: CheckUserSignedInUseCase,
) : ViewModel() {
    fun checkAutoLogin() {
        viewModelScope.launch {
            checkUserSignedInUseCase()
        }
    }
}
