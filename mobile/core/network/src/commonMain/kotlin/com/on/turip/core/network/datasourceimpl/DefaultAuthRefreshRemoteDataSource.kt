package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.data.datasource.AuthRefreshRemoteDataSource
import com.on.turip.core.data.dto.login.ReissueTokenRequest
import com.on.turip.core.data.dto.login.ReissueTokenResponse
import com.on.turip.core.network.service.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultAuthRefreshRemoteDataSource(
    private val authService: AuthService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : AuthRefreshRemoteDataSource {
    override suspend fun postReissueToken(refreshToken: String): TuripResult<ReissueTokenResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                authService.postReissueToken(ReissueTokenRequest(refreshToken))
            }
        }
}
