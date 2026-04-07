package com.on.turip.domain.login

import com.on.turip.core.result.TuripResult

interface AuthRepository {
    suspend fun login(idToken: String): TuripResult<AuthResult>

    suspend fun requestTokens(refreshToken: String): TuripResult<AuthTokens>

    suspend fun verifyToken(): TuripResult<Unit>
}
