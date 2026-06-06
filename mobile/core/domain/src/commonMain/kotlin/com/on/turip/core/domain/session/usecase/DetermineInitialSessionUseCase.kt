package com.on.turip.core.domain.session.usecase

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
        if (tokenManager.currentTokens == null) return AuthStatus.UnAuthenticated

        return authRepository.verifyToken().fold(
            onSuccess = { AuthStatus.Authenticated },
            onFailure = { errorType ->
                if (errorType is ErrorType.Auth) {
                    AuthStatus.UnAuthenticated
                } else {
                    AuthStatus.Authenticated
                }
            },
        )
    }
}
