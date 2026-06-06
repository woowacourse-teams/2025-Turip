package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.datasource.AuthRemoteDataSource
import com.on.turip.core.network.dto.login.LoginIdTokenPostRequest
import com.on.turip.core.network.dto.login.LoginJwtTokenResponse
import com.on.turip.core.network.service.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultAuthRemoteDataSource(
    private val authService: AuthService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : AuthRemoteDataSource {
    override suspend fun postIdToken(idToken: String): TuripResult<LoginJwtTokenResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                authService.postIdToken(LoginIdTokenPostRequest(idToken))
            }
        }

    override suspend fun verifyToken(): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                authService.verifyToken()
            }
        }
}
