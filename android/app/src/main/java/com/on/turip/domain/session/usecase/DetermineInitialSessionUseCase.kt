package com.on.turip.domain.session.usecase

import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.fold
import com.on.turip.domain.login.AuthRepository
import com.on.turip.domain.session.AuthStatus
import com.on.turip.domain.session.TokenManager
import javax.inject.Inject

/**
 * 앱 시작 시 토큰을 기반으로 초기 세션 상태를 결정하는 유스케이스
 */
class DetermineInitialSessionUseCase @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): AuthStatus {
        tokenManager.initialize()

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
