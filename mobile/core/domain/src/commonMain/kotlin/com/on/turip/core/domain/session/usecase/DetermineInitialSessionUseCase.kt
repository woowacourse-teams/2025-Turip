package com.on.turip.core.domain.session.usecase

import com.on.turip.core.common.NetworkError
import com.on.turip.core.common.NetworkException
import com.on.turip.core.domain.session.AuthStatus
import com.on.turip.core.domain.session.TokenManager

class DetermineInitialSessionUseCase(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): AuthStatus {
        tokenManager.initialize()
        if (tokenManager.currentTokens == null) return AuthStatus.UnAuthenticated

        return authRepository.verifyToken().fold(
            onSuccess = { AuthStatus.Authenticated },
            onFailure = { throwable ->
                if (throwable is NetworkException && throwable.networkError is NetworkError.Auth) {
                    AuthStatus.UnAuthenticated
                } else {
                    AuthStatus.Authenticated
                }
            },
        )
    }
}
