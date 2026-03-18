package com.on.turip.ui.compose.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.navigation.InitialNavigationTarget
import com.on.turip.domain.invitation.repository.DeferredDeepLinkRepository
import com.on.turip.domain.session.SessionState
import com.on.turip.domain.session.usecase.DetermineInitialSessionUseCase
import com.on.turip.ui.common.extensions.toUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val determineInitialSessionUseCase: DetermineInitialSessionUseCase,
    private val deferredDeepLinkRepository: DeferredDeepLinkRepository,
) : ViewModel() {
    private val _uiEffect: Channel<SplashUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<SplashUiEffect> = _uiEffect.receiveAsFlow()

    // 업데이트 체크 성공과 업데이트 런처 Result에서의 중복 요청 가능성을 제거
    private val hasDeterminedStartDestination: AtomicBoolean = AtomicBoolean(false)

    /**
     * 앱 최초 진입 목적지를 결정한다.
     *
     * 1. 일반 deep link(intent)
     * 2. deferred deep link(install referrer)
     * 3. 세션 기반 기본 진입(Home/Login)
     */
    fun determineStartDestination(deepLinkUrl: String?) {
        if (!hasDeterminedStartDestination.compareAndSet(false, true)) return

        viewModelScope.launch {
            try {
                val trimmedDeepLinkUrl: String? = deepLinkUrl?.trim()?.takeIf(String::isNotEmpty)
                if (trimmedDeepLinkUrl != null) {
                    _uiEffect.send(
                        SplashUiEffect.NavigateToMain(InitialNavigationTarget.InvitationEntry(deepLinkUrl = trimmedDeepLinkUrl)),
                    )
                    return@launch
                }

                val sessionState: SessionState
                val deferredDeepLinkUrl: String?
                coroutineScope {
                    val sessionStateDeferred = async { determineInitialSessionUseCase() }
                    val deferredDeepLinkUrlDeferred =
                        async { deferredDeepLinkRepository.resolveDeferredInvitationToken().getOrNull()?.toUrl() }
                    sessionState = sessionStateDeferred.await()
                    deferredDeepLinkUrl = deferredDeepLinkUrlDeferred.await()
                }

                val initialNavigationTarget: InitialNavigationTarget =
                    deferredDeepLinkUrl?.let { url: String ->
                        InitialNavigationTarget.InvitationEntry(deepLinkUrl = url)
                    } ?: when (sessionState) {
                        SessionState.Member -> InitialNavigationTarget.Home
                        SessionState.Guest, SessionState.Uninitialized -> InitialNavigationTarget.Login
                    }

                _uiEffect.send(SplashUiEffect.NavigateToMain(initialNavigationTarget))
            } catch (exception: Throwable) {
                Timber.e(exception, "초기 진입 목적지 결정 실패")
                hasDeterminedStartDestination.set(false)
            }
        }
    }
}
