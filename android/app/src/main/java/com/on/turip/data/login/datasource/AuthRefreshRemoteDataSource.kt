package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.dto.ReissueTokenResponse

interface AuthRefreshRemoteDataSource {
    suspend fun postReissueToken(refreshToken: String): TuripResult<ReissueTokenResponse>
}
