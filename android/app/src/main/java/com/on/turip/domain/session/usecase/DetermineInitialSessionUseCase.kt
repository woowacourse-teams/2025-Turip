package com.on.turip.domain.session.usecase

import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.fold
import com.on.turip.domain.login.AuthRepository
import com.on.turip.domain.login.AuthTokens
import com.on.turip.domain.session.SessionState
import com.on.turip.domain.session.SessionStore
import com.on.turip.domain.session.TokenManager
import javax.inject.Inject

/**
 * 앱 시작 시 토큰을 기반으로 초기 세션 상태를 결정하는 유스케이스
 */
class DetermineInitialSessionUseCase @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionStore: SessionStore,
    private val authRepository: AuthRepository,
    private val moveToGuestUseCase: MoveToGuestUseCase,
) {
    suspend operator fun invoke(): SessionState {
        tokenManager.initialize()

        val tokens: AuthTokens? = tokenManager.currentTokens()
        if (tokens == null) {
            sessionStore.setGuest()
            return SessionState.Guest
        }

        // 토큰이 유효한지 검증
        authRepository.getTokenVerification(tokens.accessToken).fold(
            onSuccess = {
                sessionStore.setMember()
                return SessionState.Member
            },
            onFailure = { errorType ->
                when (errorType) {
                    is ErrorType.Auth -> {
                        moveToGuestUseCase()
                        return SessionState.Guest
                    }

                    else -> {
                        // Auth 관련 오류가 아닌 경우는 토큰 초기화하지 않고 멤버로 내려주기
                        sessionStore.setMember()
                        return SessionState.Member
                    }
                }
            },
        )
    }
}
