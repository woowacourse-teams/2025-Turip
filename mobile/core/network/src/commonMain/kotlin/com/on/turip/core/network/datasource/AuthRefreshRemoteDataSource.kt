package com.on.turip.core.network.datasource

import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.dto.login.ReissueTokenResponse

interface AuthRefreshRemoteDataSource {
    suspend fun postReissueToken(refreshToken: String): TuripResult<ReissueTokenResponse>
}
