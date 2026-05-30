package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.login.LoginIdTokenPostRequest
import com.on.turip.core.network.dto.login.LoginJwtTokenResponse
import com.on.turip.core.network.dto.login.ReissueTokenRequest
import com.on.turip.core.network.dto.login.ReissueTokenResponse

interface AuthDatasource {
    suspend fun postLogin(body: LoginIdTokenPostRequest): LoginJwtTokenResponse

    suspend fun postGuestLogin(): LoginJwtTokenResponse

    suspend fun postReissueToken(body: ReissueTokenRequest): ReissueTokenResponse

    suspend fun verifyToken()
}
