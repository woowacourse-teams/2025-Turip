package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.auth.LoginIdTokenPostRequest
import com.on.turip.core.network.dto.auth.LoginJwtTokenResponse
import com.on.turip.core.network.dto.auth.ReissueTokenRequest
import com.on.turip.core.network.dto.auth.ReissueTokenResponse
import com.on.turip.core.network.service.AuthService

class DefaultAuthDatasource(
    private val authService: AuthService,
) : AuthDatasource {
    override suspend fun postLogin(body: LoginIdTokenPostRequest): LoginJwtTokenResponse =
        authService.postLogin(body)

    override suspend fun postGuestLogin(): LoginJwtTokenResponse =
        authService.postGuestLogin()

    override suspend fun postReissueToken(body: ReissueTokenRequest): ReissueTokenResponse =
        authService.postReissueToken(body)

    override suspend fun verifyToken() = authService.verifyToken()
}
