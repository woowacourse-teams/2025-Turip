package com.on.turip.core.data.datasource

import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.data.dto.login.LoginJwtTokenResponse

interface AuthRemoteDataSource {
    suspend fun postIdToken(idToken: String): TuripResult<LoginJwtTokenResponse>

    suspend fun postAppleIdToken(idToken: String, nonce: String): TuripResult<LoginJwtTokenResponse>

    suspend fun verifyToken(): TuripResult<Unit>
}
