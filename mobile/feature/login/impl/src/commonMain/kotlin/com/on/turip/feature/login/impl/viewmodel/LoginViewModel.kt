package com.on.turip.feature.login.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.AuthRepository
import com.on.turip.core.domain.session.SessionManager
import com.on.turip.core.ui.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(
    private val deepLinkUrl: String?,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<LoginIntent, LoginState, LoginEffect>(LoginState()) {

    override fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.ClickGoogleLogin -> handleGoogleLogin(intent.idToken)
            LoginIntent.ClickGuestLogin -> handleGuestLogin()
        }
    }

    private fun handleGoogleLogin(idToken: String) {
        if (currentState.isLoading) return
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            authRepository.loginWithGoogleIdToken(
                idToken = idToken,
                clientId = GOOGLE_CLIENT_ID,
            ).onSuccess {
                sessionManager.switchToMember()
                navigateAfterLogin()
            }.onFailure {
                updateState { copy(isLoading = false) }
                emitEffect(LoginEffect.ShowSnackbar("로그인에 실패했습니다. 다시 시도해 주세요."))
            }
        }
    }

    private fun handleGuestLogin() {
        if (currentState.isLoading) return
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            authRepository.loginAsGuest()
                .onSuccess {
                    sessionManager.switchToGuest()
                    navigateAfterLogin()
                }.onFailure {
                    updateState { copy(isLoading = false) }
                    emitEffect(LoginEffect.ShowSnackbar("게스트 로그인에 실패했습니다. 다시 시도해 주세요."))
                }
        }
    }

    private fun navigateAfterLogin() {
        val trimmedDeepLinkUrl = deepLinkUrl?.trim()?.takeIf(String::isNotEmpty)
        if (trimmedDeepLinkUrl != null) {
            emitEffect(LoginEffect.NavigateToInvitationEntry(trimmedDeepLinkUrl))
        } else {
            emitEffect(LoginEffect.NavigateToHome)
        }
    }

    private companion object {
        // TODO: Replace with actual Google OAuth 2.0 client ID
        const val GOOGLE_CLIENT_ID = ""
    }
}
