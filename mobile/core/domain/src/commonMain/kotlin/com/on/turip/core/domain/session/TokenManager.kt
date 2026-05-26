package com.on.turip.core.domain.session

import com.on.turip.core.model.AuthTokens

interface TokenManager {
    val currentTokens: AuthTokens?

    suspend fun initialize()

    suspend fun setTokens(tokens: AuthTokens): Result<Unit>

    suspend fun clearTokens(): Result<Unit>
}
