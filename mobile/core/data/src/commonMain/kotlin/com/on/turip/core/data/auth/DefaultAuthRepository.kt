package com.on.turip.core.data.auth

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.domain.auth.AuthRepository
import com.on.turip.core.model.AuthTokens
import com.on.turip.core.network.datasource.AuthDatasource
import com.on.turip.core.network.dto.auth.LoginIdTokenPostRequest
import com.on.turip.core.network.dto.auth.ReissueTokenRequest

class DefaultAuthRepository(
    private val authDatasource: AuthDatasource,
) : AuthRepository {
    override suspend fun loginWithGoogleIdToken(idToken: String, clientId: String): Result<AuthTokens> =
        safeApiCall {
            authDatasource.postLogin(LoginIdTokenPostRequest(idToken, clientId)).toDomain()
        }

    override suspend fun loginAsGuest(): Result<AuthTokens> =
        safeApiCall {
            authDatasource.postGuestLogin().toDomain()
        }

    override suspend fun requestTokens(refreshToken: String): Result<AuthTokens> =
        safeApiCall {
            authDatasource.postReissueToken(ReissueTokenRequest(refreshToken)).toDomain()
        }

    override suspend fun verifyToken(): Result<Unit> = safeApiCall { authDatasource.verifyToken() }

    private fun com.on.turip.core.network.dto.auth.LoginJwtTokenResponse.toDomain() =
        AuthTokens(accessToken = accessToken, refreshToken = refreshToken)

    private fun com.on.turip.core.network.dto.auth.ReissueTokenResponse.toDomain() =
        AuthTokens(accessToken = accessToken, refreshToken = refreshToken)
}
