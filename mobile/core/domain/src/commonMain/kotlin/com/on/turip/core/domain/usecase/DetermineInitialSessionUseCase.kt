package com.on.turip.core.domain.usecase

import com.on.turip.core.domain.repository.AuthRepository
import com.on.turip.core.domain.session.AuthStatus
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.fold

class DetermineInitialSessionUseCase(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): AuthStatus {
        tokenManager.initialize()

        // 게스트 모드 상태인 경우
        if (tokenManager.currentTokens == null) return AuthStatus.UnAuthenticated

        // 토큰이 유효한지 검증
        authRepository.verifyToken().fold(
            onSuccess = {
                return AuthStatus.Authenticated
            },
            onFailure = { errorType ->
                return when (errorType) {
                    is ErrorType.Auth -> {
                        AuthStatus.UnAuthenticated
                    }

                    else -> {
                        // Auth 관련 오류가 아닌 경우는 멤버로 내려주기
                        AuthStatus.Authenticated
                    }
                }
            },
        )
    }
}
