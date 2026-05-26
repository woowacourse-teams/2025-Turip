package com.on.turip.core.domain.auth

import com.on.turip.core.model.AuthTokens

interface AuthRepository {
    suspend fun loginWithGoogleIdToken(idToken: String, clientId: String): Result<AuthTokens>
    suspend fun loginAsGuest(): Result<AuthTokens>
    suspend fun requestTokens(refreshToken: String): Result<AuthTokens>
}
