package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.login.LoginIdTokenPostRequest
import com.on.turip.core.network.dto.login.LoginJwtTokenResponse
import com.on.turip.core.network.dto.login.ReissueTokenRequest
import com.on.turip.core.network.dto.login.ReissueTokenResponse
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
