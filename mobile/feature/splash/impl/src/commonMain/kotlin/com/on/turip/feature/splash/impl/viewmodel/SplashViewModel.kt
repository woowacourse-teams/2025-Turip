package com.on.turip.feature.splash.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.DeferredDeepLinkRepository
import com.on.turip.core.domain.session.AuthStatus
import com.on.turip.core.domain.session.SessionManager
import com.on.turip.core.domain.session.usecase.DetermineInitialSessionUseCase
import com.on.turip.core.model.SessionState
import com.on.turip.core.ui.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SplashViewModel(
    private val determineInitialSessionUseCase: DetermineInitialSessionUseCase,
    private val sessionManager: SessionManager,
    private val deferredDeepLinkRepository: DeferredDeepLinkRepository,
) : BaseViewModel<SplashIntent, SplashState, SplashEffect>(SplashState()) {
    @kotlin.concurrent.Volatile
    private var hasDetermined = false

    override fun onIntent(intent: SplashIntent) = Unit

    fun determineStartDestination(deepLinkUrl: String?) {
        if (hasDetermined) return
        hasDetermined = true

        viewModelScope.launch {
            try {
                val trimmedDeepLinkUrl = deepLinkUrl?.trim()?.takeIf(String::isNotEmpty)
                if (trimmedDeepLinkUrl != null) {
                    switchSession(determineInitialSessionUseCase())
                    emitEffect(SplashEffect.NavigateToInvitationEntry(trimmedDeepLinkUrl))
                    return@launch
                }

                val deferredDeepLinkUrl = coroutineScope {
                    val authStatusDeferred = async { determineInitialSessionUseCase() }
                    val deferredUrlDeferred = async {
                        deferredDeepLinkRepository.resolveDeferredInvitationToken().getOrNull()
                    }
                    switchSession(authStatusDeferred.await())
                    deferredUrlDeferred.await()
                }

                if (deferredDeepLinkUrl != null) {
                    emitEffect(SplashEffect.NavigateToInvitationEntry(deferredDeepLinkUrl))
                } else {
                    when (sessionManager.state.value) {
                        SessionState.LoggedIn -> emitEffect(SplashEffect.NavigateToMain)
                        SessionState.Guest, SessionState.Unknown -> emitEffect(SplashEffect.NavigateToLogin)
                    }
                }
            } catch (throwable: Throwable) {
                hasDetermined = false
            }
        }
    }

    private suspend fun switchSession(authStatus: AuthStatus) {
        when (authStatus) {
            AuthStatus.Authenticated -> sessionManager.switchToMember()
            AuthStatus.UnAuthenticated -> sessionManager.switchToGuest()
        }
    }
}
