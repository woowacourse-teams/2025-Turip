package com.on.turip.core.network.datasource

import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.dto.login.LoginJwtTokenResponse

interface AuthRemoteDataSource {
    suspend fun postIdToken(idToken: String): TuripResult<LoginJwtTokenResponse>

    suspend fun verifyToken(): TuripResult<Unit>
}
