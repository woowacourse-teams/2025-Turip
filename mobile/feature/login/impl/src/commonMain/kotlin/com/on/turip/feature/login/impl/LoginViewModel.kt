package com.on.turip.feature.login.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.onFailure
import com.on.turip.core.model.result.onFailureWithCause
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.domain.login.GuestRepository
import com.on.turip.domain.login.MemberRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val memberRepository: MemberRepository,
    private val guestRepository: GuestRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState.IDLE)
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _uiEffect: Channel<LoginUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<LoginUiEffect> = _uiEffect.receiveAsFlow()

    private var isLoginInProgress: Boolean = false

    fun initDeepLinkUrl(deepLinkUrl: String?) {
        val trimmedDeepLinkUrl: String? = deepLinkUrl?.trim()?.takeIf(String::isNotEmpty)
        if (_uiState.value.deepLinkUrl == trimmedDeepLinkUrl) return

        _uiState.update { it.copy(deepLinkUrl = trimmedDeepLinkUrl) }

        if (!trimmedDeepLinkUrl.isNullOrBlank()) {
            viewModelScope.launch {
                _uiEffect.send(LoginUiEffect.RequestAutoLogin)
            }
        }
    }

    fun updateHelpTextVisible(show: Boolean) {
        _uiState.update { it.copy(showHelpText = show) }
    }

    fun loginWithGoogle(googleCredentialManager: GoogleCredentialManager) {
        if (isLoginInProgress) return
        isLoginInProgress = true
        viewModelScope.launch {
            try {
                googleCredentialManager
                    .getIdToken()
                    .onSuccess { idToken: String ->
                        loginUseCase(idToken)
                            .onSuccess { isMigrationDecided: Boolean ->
                                if (!isMigrationDecided) {
                                    _uiState.update { it.copy(showMigrationDialog = true) }
                                } else {
                                    _uiEffect.send(LoginUiEffect.NavigateToMain(_uiState.value.deepLinkUrl))
                                }
                            }.onFailureWithCause { errorType, cause ->
                                Napier.e(
                                    message = "Google 로그인 API 실패. errorType=$errorType",
                                    throwable = cause,
                                )
                                handleError(errorType)
                            }
                    }.onFailureWithCause { errorType: ErrorType, cause: Throwable ->
                        // 로그인 도중 사용자가 포기한 경우는 예외로 판단하지 않고 무시
                        if (errorType == ErrorType.Cancelled) {
                            Napier.d("Google 로그인 취소")
                            return@onFailureWithCause
                        }

                        handleError(errorType)
                        Napier.e(
                            message = "googleCredentialManager 에서 IdToken 불러오기 실패. errorType=$errorType",
                            throwable = cause,
                        )
                    }
            } finally {
                isLoginInProgress = false
            }
        }
    }

    fun loginWithApple(appleCredentialManager: AppleCredentialManager) {
        if (isLoginInProgress) return
        isLoginInProgress = true
        viewModelScope.launch {
            try {
                appleCredentialManager
                    .getCredential()
                    .onSuccess { credential: AppleCredential ->
                        loginUseCase.loginWithApple(idToken = credential.idToken, nonce = credential.rawNonce)
                            .onSuccess { isMigrationDecided: Boolean ->
                                if (!isMigrationDecided) {
                                    _uiState.update { it.copy(showMigrationDialog = true) }
                                } else {
                                    _uiEffect.send(LoginUiEffect.NavigateToMain(_uiState.value.deepLinkUrl))
                                }
                            }.onFailureWithCause { errorType, cause ->
                                Napier.e(
                                    message = "Apple 로그인 API 실패. errorType=$errorType",
                                    throwable = cause,
                                )
                                handleError(errorType)
                            }
                    }.onFailureWithCause { errorType: ErrorType, cause: Throwable ->
                        if (errorType == ErrorType.Cancelled) {
                            Napier.d("Apple 로그인 취소")
                            return@onFailureWithCause
                        }

                        handleError(errorType)
                        Napier.e(
                            message = "appleCredentialManager 에서 credential 불러오기 실패. errorType=$errorType",
                            throwable = cause,
                        )
                    }
            } finally {
                isLoginInProgress = false
            }
        }
    }

    fun confirmMigration() {
        viewModelScope.launch {
            memberRepository
                .updateMigration()
                .onSuccess {
                    _uiState.update { it.copy(showMigrationDialog = false) }
                    _uiEffect.send(LoginUiEffect.NavigateToMain(_uiState.value.deepLinkUrl))
                }.onFailure { errorType ->
                    Napier.e("마이그레이션 실패")
                    _uiState.update { it.copy(showMigrationDialog = false) }
                    handleError(errorType)
                }
        }
    }

    fun rejectMigration() {
        viewModelScope.launch {
            memberRepository
                .rejectMigration()
                .onSuccess {
                    _uiState.update { it.copy(showMigrationDialog = false) }
                    clearGuestData()
                }.onFailure { errorType ->
                    Napier.e("마이그레이션 거절 실패")
                    _uiState.update { it.copy(showMigrationDialog = false) }
                    handleError(errorType)
                }
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            sessionManager.switchToGuest()
            _uiEffect.send(LoginUiEffect.NavigateToMain())
        }
    }

    private suspend fun clearGuestData() {
        guestRepository
            .deleteGuest()
            .onSuccess {
                _uiEffect.send(LoginUiEffect.NavigateToMain(_uiState.value.deepLinkUrl))
            }.onFailure {
                Napier.e("게스트 데이터 삭제 실패")
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

            UiError.Feature.Cancelled -> {}

            else -> {
                _uiEffect.send(LoginUiEffect.ShowError(ErrorUiState.Unexpected))
            }
        }
    }
}
