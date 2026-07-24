package com.on.turip.core.domain.repository

import com.on.turip.core.model.login.AuthResult
import com.on.turip.core.model.login.AuthTokens
import com.on.turip.core.model.result.TuripResult

interface AuthRepository {
    suspend fun login(idToken: String): TuripResult<AuthResult>

    suspend fun loginWithApple(idToken: String, nonce: String): TuripResult<AuthResult>

    suspend fun requestTokens(refreshToken: String): TuripResult<AuthTokens>

    suspend fun verifyToken(): TuripResult<Unit>
}
