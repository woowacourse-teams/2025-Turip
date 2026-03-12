package com.on.turip.ui.compose.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.navigation.InitialNavigationTarget
import com.on.turip.domain.session.SessionState
import com.on.turip.domain.session.usecase.DetermineInitialSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val determineInitialSessionUseCase: DetermineInitialSessionUseCase,
) : ViewModel() {
    private val _uiEffect: Channel<SplashUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<SplashUiEffect> = _uiEffect.receiveAsFlow()

    // 업데이트 체크 성공과 업데이트 런처 Result에서의 중복 요청 가능성을 제거
    private val hasDeterminedStartDestination: AtomicBoolean = AtomicBoolean(false)

    fun determineStartDestination(deepLinkUrl: String?) {
        if (!hasDeterminedStartDestination.compareAndSet(false, true)) return

        viewModelScope.launch {
            val sessionState: SessionState = determineInitialSessionUseCase()

            val trimmedDeepLinkUrl: String? = deepLinkUrl?.trim()?.takeIf(String::isNotEmpty)
            val initialNavigationTarget: InitialNavigationTarget =
                trimmedDeepLinkUrl?.let { url: String ->
                    InitialNavigationTarget.InvitationEntry(deepLinkUrl = url)
                } ?: when (sessionState) {
                    SessionState.Member -> InitialNavigationTarget.Home
                    SessionState.Guest, SessionState.Uninitialized -> InitialNavigationTarget.Login
                }

            _uiEffect.send(SplashUiEffect.NavigateToMain(initialNavigationTarget))
        }
    }
}
