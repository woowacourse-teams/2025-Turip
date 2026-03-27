package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.dto.ReissueTokenRequest
import com.on.turip.data.login.dto.ReissueTokenResponse
import com.on.turip.data.login.service.AuthService
import com.on.turip.data.result.safeApiCall
import com.on.turip.di.qualifier.NoAuthAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultAuthRefreshRemoteRemoteDataSource @Inject constructor(
    @NoAuthAuthService private val authService: AuthService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : AuthRefreshRemoteDataSource {
    override suspend fun postReissueToken(refreshToken: String): TuripResult<ReissueTokenResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                authService.postReissueToken(ReissueTokenRequest(refreshToken))
            }
        }
}
