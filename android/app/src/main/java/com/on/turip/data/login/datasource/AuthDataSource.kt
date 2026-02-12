package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.dto.LoginJwtTokenResponse
import com.on.turip.data.login.dto.ReissueTokenResponse

interface AuthDataSource {
    suspend fun postIdToken(idToken: String): TuripResult<LoginJwtTokenResponse>

    suspend fun postReissueToken(refreshToken: String): TuripResult<ReissueTokenResponse>

    suspend fun getTokenVerification(accessToken: String): TuripResult<Unit>
}
