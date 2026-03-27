package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.dto.LoginIdTokenPostRequest
import com.on.turip.data.login.dto.LoginJwtTokenResponse
import com.on.turip.data.login.service.AuthService
import com.on.turip.data.result.safeApiCall
import com.on.turip.di.qualifier.DefaultAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AuthRemoteDataSource @Inject constructor(
    @DefaultAuthService private val authService: AuthService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : AuthDataSource {
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
