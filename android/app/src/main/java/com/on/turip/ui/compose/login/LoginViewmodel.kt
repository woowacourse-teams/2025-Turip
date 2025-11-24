package com.on.turip.ui.compose.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.data.login.datasource.GoogleCredentialManager
import com.on.turip.domain.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewmodel @Inject constructor(
    val loginRepository: LoginRepository,
) : ViewModel() {
    fun login(googleCredentialManager: GoogleCredentialManager) {
        viewModelScope.launch {
            googleCredentialManager
                .getIdToken()
                .onSuccess { result: GoogleIdTokenCredential ->
                    loginRepository.login(result.idToken)
                }.onFailure {
                    Timber.e("IdToken불러오기 실패")
                }
        }
    }
}
