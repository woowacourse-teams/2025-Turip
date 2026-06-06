package com.on.turip.core.data.datasource

import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.data.dto.login.ReissueTokenResponse

interface AuthRefreshRemoteDataSource {
    suspend fun postReissueToken(refreshToken: String): TuripResult<ReissueTokenResponse>
}
