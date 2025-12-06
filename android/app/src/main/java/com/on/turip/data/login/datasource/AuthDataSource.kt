package com.on.turip.data.login.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.login.dto.LoginJwtTokenResponse
import com.on.turip.data.login.dto.ReissueTokenResponse

interface AuthDataSource {
    suspend fun postIdToken(idToken: String): TuripCustomResult<LoginJwtTokenResponse>

    suspend fun postReissueToken(refreshToken: String): TuripCustomResult<ReissueTokenResponse>

    suspend fun getTokenVerification(accessToken: String): TuripCustomResult<Unit>
}
