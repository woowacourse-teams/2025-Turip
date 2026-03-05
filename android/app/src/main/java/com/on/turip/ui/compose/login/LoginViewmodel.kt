package com.on.turip.ui.compose.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.data.login.datasource.GoogleCredentialManager
import com.on.turip.domain.login.GuestRepository
import com.on.turip.domain.login.MemberRepository
import com.on.turip.domain.login.usecase.LoginUseCase
import com.on.turip.domain.session.usecase.SwitchToGuestUseCase
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
    private val loginUseCase: LoginUseCase,
    private val memberRepository: MemberRepository,
    private val guestRepository: GuestRepository,
    private val switchToGuestUseCase: SwitchToGuestUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState.IDLE)
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _uiEffect: Channel<LoginUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<LoginUiEffect> = _uiEffect.receiveAsFlow()

    fun updateHelpTextVisible(show: Boolean) {
        _uiState.update { it.copy(showHelpText = show) }
    }

    fun loginWithGoogle(googleCredentialManager: GoogleCredentialManager) {
        viewModelScope.launch {
            googleCredentialManager
                .getIdToken()
                .onSuccess { idToken: String ->
                    loginUseCase(idToken)
                        .onSuccess { isNewMember: Boolean ->
                            if (isNewMember) {
                                _uiState.update { it.copy(showMigrationDialog = true) }
                            } else {
                                _uiEffect.send(LoginUiEffect.NavigateToMain)
                            }
                        }.onFailure { errorType ->
                            handleError(errorType)
                        }
                }.onFailure { errorType: ErrorType ->
                    handleError(errorType)
                    Timber.e("googleCredentialManager 에서 IdToken 불러오기 실패")
                }
        }
    }

    fun confirmMigration() {
        viewModelScope.launch {
            memberRepository
                .updateMigration()
                .onSuccess {
                    _uiState.update { it.copy(showMigrationDialog = false) }
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

    fun continueAsGuest() {
        viewModelScope.launch {
            switchToGuestUseCase()
            _uiEffect.send(LoginUiEffect.NavigateToMain)
        }
    }

    private suspend fun handleError(errorType: ErrorType) {
        val uiError = errorType.toUiError()
        when (uiError) {
            UiError.Global.Network -> {
                _uiEffect.send(LoginUiEffect.ShowError(ErrorUiState.Network))
            }

            UiError.Global.Server -> {
                _uiEffect.send(LoginUiEffect.ShowError(ErrorUiState.Server))
            }

            else -> {
                _uiEffect.send(LoginUiEffect.ShowError(ErrorUiState.Unexpected))
            }
        }
    }
}
