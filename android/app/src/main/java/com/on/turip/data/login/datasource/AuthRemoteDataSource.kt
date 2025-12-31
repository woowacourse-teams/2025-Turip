package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.result.safeApiCall
import com.on.turip.data.login.dto.LoginIdTokenPostRequest
import com.on.turip.data.login.dto.LoginJwtTokenResponse
import com.on.turip.data.login.dto.ReissueTokenRequest
import com.on.turip.data.login.dto.ReissueTokenResponse
import com.on.turip.data.login.service.AuthService
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRemoteDataSource @Inject constructor(
    private val authService: AuthService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : AuthDataSource {
    override suspend fun postIdToken(idToken: String): TuripResult<LoginJwtTokenResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                authService.postIdToken(LoginIdTokenPostRequest(idToken))
            }
        }

    override suspend fun postReissueToken(refreshToken: String): TuripResult<ReissueTokenResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                authService.postReissueToken(ReissueTokenRequest(refreshToken))
            }
        }

    override suspend fun getTokenVerification(accessToken: String): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                authService.getTokenVerification(AUTHORIZATION_PREFIX + accessToken)
            }
        }

    companion object {
        private const val AUTHORIZATION_PREFIX = "Bearer "
    }
}
