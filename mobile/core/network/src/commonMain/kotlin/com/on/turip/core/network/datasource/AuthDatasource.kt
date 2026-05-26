package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.auth.LoginIdTokenPostRequest
import com.on.turip.core.network.dto.auth.LoginJwtTokenResponse
import com.on.turip.core.network.dto.auth.ReissueTokenRequest
import com.on.turip.core.network.dto.auth.ReissueTokenResponse

interface AuthDatasource {
    suspend fun postLogin(body: LoginIdTokenPostRequest): LoginJwtTokenResponse

    suspend fun postGuestLogin(): LoginJwtTokenResponse

    suspend fun postReissueToken(body: ReissueTokenRequest): ReissueTokenResponse

    suspend fun verifyToken()
}
