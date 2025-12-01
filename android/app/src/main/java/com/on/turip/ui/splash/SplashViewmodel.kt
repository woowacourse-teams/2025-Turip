package com.on.turip.ui.splash

import androidx.lifecycle.ViewModel
import com.on.turip.domain.login.usecase.CheckUserSignedInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewmodel @Inject constructor(
    private val checkUserSignedInUseCase: CheckUserSignedInUseCase,
) : ViewModel() {
    suspend fun checkAutoLogin() {
        checkUserSignedInUseCase()
    }
}
