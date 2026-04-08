package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.dto.LoginJwtTokenResponse

interface AuthRemoteDataSource {
    suspend fun postIdToken(idToken: String): TuripResult<LoginJwtTokenResponse>

    suspend fun verifyToken(): TuripResult<Unit>
}
