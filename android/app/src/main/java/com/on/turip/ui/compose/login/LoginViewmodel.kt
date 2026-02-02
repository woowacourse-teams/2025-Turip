package com.on.turip.ui.compose.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.data.login.datasource.GoogleCredentialManager
import com.on.turip.domain.login.GuestRepository
import com.on.turip.domain.login.MemberRepository
import com.on.turip.domain.login.usecase.LoginUserUseCase
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
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
    private val loginUserUseCase: LoginUserUseCase,
    private val memberRepository: MemberRepository,
    private val guestRepository: GuestRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState.IDLE)
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _uiEffect: Channel<LoginUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<LoginUiEffect> = _uiEffect.receiveAsFlow()

    fun updateHelpTextVisible(show: Boolean) {
        _uiState.update { it.copy(showHelpText = show) }
    }

    fun onGoogleLogin(googleCredentialManager: GoogleCredentialManager) {
        viewModelScope.launch {
            googleCredentialManager
                .getIdToken()
                .onSuccess { result: GoogleIdTokenCredential ->
                    loginUserUseCase(result.idToken)
                        .onSuccess { isNewMember: Boolean ->
                            AuthState.change(UserType.MEMBER)
                            if (isNewMember) {
                                showMigrationDialog(true)
                            } else {
                                _uiEffect.send(LoginUiEffect.NavigateToMain)
                            }
                        }.onFailure {
                            Timber.e("Token 저장 실패")
                            _uiEffect.send(LoginUiEffect.ShowError(ErrorUiState.Server))
                        }
                }.onFailure { errorType: ErrorType ->
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _uiEffect.send(LoginUiEffect.ShowError(ErrorUiState.Network))
                            }

                            UiError.Global.Server -> {
                                _uiEffect.send(LoginUiEffect.ShowError(ErrorUiState.Server))
                            }

                            UiError.Global.TokenExpired -> {
                                Unit
                            }
                        }
                    }
                    Timber.e("IdToken불러오기 실패")
                }
        }
    }

    private fun showMigrationDialog(show: Boolean) {
        _uiState.update { it.copy(showMigrationDialog = show) }
    }

    fun migration() {
        viewModelScope.launch {
            memberRepository
                .updateMigration()
                .onSuccess {
                    _uiEffect.send(LoginUiEffect.NavigateToMain)
                }.onFailure {
                    Timber.e("마이그레이션 실패")
                }
        }
    }

    fun clearGuestData() {
        viewModelScope.launch {
            guestRepository
                .deleteGuest()
                .onSuccess {
                    _uiEffect.send(LoginUiEffect.NavigateToMain)
                }.onFailure {
                    Timber.e("게스트 데이터 삭제 실패")
                }
        }
    }

    fun onGuestLogin() {
        viewModelScope.launch {
            AuthState.change(UserType.GUEST)
            _uiEffect.send(LoginUiEffect.NavigateToMain)
        }
    }
}
