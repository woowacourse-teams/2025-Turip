package com.on.turip.feature.splash.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.DeferredDeepLinkRepository
import com.on.turip.core.domain.session.AuthStatus
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.domain.usecase.DetermineInitialSessionUseCase
import com.on.turip.core.model.turip.TuripInvitationToken
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val determineInitialSessionUseCase: DetermineInitialSessionUseCase,
    private val sessionManager: SessionManager,
    private val deferredDeepLinkRepository: DeferredDeepLinkRepository,
) : ViewModel() {
    private val _uiEffect: Channel<SplashUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<SplashUiEffect> = _uiEffect.receiveAsFlow()

    // 업데이트 체크 성공과 업데이트 런처 Result에서의 중복 요청 가능성을 제거
    private var hasDeterminedStartDestination: Boolean = false

    /**
     * 앱 최초 진입 목적지를 결정한다.
     *
     * 1. 일반 deep link(intent)
     * 2. deferred deep link(install referrer)
     * 3. 세션 기반 기본 진입(Home/Login)
     */
    fun determineStartDestination(deepLinkUrl: String?) {
        if (hasDeterminedStartDestination) return
        hasDeterminedStartDestination = true

        viewModelScope.launch {
            try {
                val trimmedDeepLinkUrl: String? = deepLinkUrl?.trim()?.takeIf(String::isNotEmpty)
                if (trimmedDeepLinkUrl != null) {
                    switchSession(authStatus = determineInitialSessionUseCase())

                    _uiEffect.send(
                        SplashUiEffect.NavigateToInvitationEntry(
                            deepLinkUrl = trimmedDeepLinkUrl,
                        ),
                    )
                    return@launch
                }

                val deferredDeepLinkUrl: String? =
                    coroutineScope {
                        val authStatusDeferred = async { determineInitialSessionUseCase() }
                        val deferredDeepLinkUrlDeferred =
                            async {
                                deferredDeepLinkRepository
                                    .resolveDeferredInvitationToken()
                                    .getOrNull()
                                    ?.toInvitationUrl()
                            }

                        switchSession(authStatus = authStatusDeferred.await())
                        deferredDeepLinkUrlDeferred.await()
                    }

                if (deferredDeepLinkUrl != null) {
                    _uiEffect.send(SplashUiEffect.NavigateToInvitationEntry(deepLinkUrl = deferredDeepLinkUrl))
                } else {
                    when (sessionManager.state.value) {
                        SessionState.Member -> {
                            _uiEffect.send(SplashUiEffect.NavigateToMain)
                        }

                        SessionState.Guest, SessionState.Uninitialized -> {
                            _uiEffect.send(SplashUiEffect.NavigateToLogin)
                        }
                    }
                }
            } catch (exception: Throwable) {
                Napier.e("초기 진입 목적지 결정 실패", exception)
                hasDeterminedStartDestination = false
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

private fun TuripInvitationToken.toInvitationUrl(): String =
    "https://invite.turip.kro.kr/invitations?token=$value"
