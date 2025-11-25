package com.on.turip.ui.compose.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.data.login.datasource.GoogleCredentialManager
import com.on.turip.domain.login.LoginRepository
import com.on.turip.domain.login.usecase.LoginUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewmodel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val loginUserUseCase: LoginUserUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState.EMPTY)
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _uiEvent: Channel<LoginUiEvent> = Channel(Channel.BUFFERED)
    val uiEvent: Flow<LoginUiEvent> = _uiEvent.receiveAsFlow()

    fun updateHelpTextVisible(show: Boolean) {
        _uiState.update { it.copy(showHelpText = show) }
    }

    fun onGoogleLogin(googleCredentialManager: GoogleCredentialManager) {
        viewModelScope.launch {
            googleCredentialManager
                .getIdToken()
                .onSuccess { result: GoogleIdTokenCredential ->
                    loginUserUseCase(result.idToken)
                        .onSuccess {
                            AuthState.change(UserType.MEMBER)
                            _uiEvent.send(LoginUiEvent.NavigateToMain)
                        }.onFailure {
                            Timber.e("Token 저장 실패")
                        }
                }.onFailure {
                    Timber.e("IdToken불러오기 실패")
                }
        }
    }

    fun onGuestLogin() {
        viewModelScope.launch {
            // TODO : DataStore 에 게스트 상태 저장
            AuthState.change(UserType.GUEST)
            _uiEvent.send(LoginUiEvent.NavigateToMain)
        }
    }
}
