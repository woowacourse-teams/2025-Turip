package com.on.turip.data.login.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.login.dto.LoginIdTokenPostRequest
import com.on.turip.data.login.dto.LoginJwtTokenResponse
import com.on.turip.data.login.dto.ReissueTokenRequest
import com.on.turip.data.login.dto.ReissueTokenResponse
import com.on.turip.data.login.service.LoginService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class LoginRemoteDatasource @Inject constructor(
    private val loginService: LoginService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : LoginDatasource {
    override suspend fun postIdToken(idToken: String): TuripCustomResult<LoginJwtTokenResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                loginService.postIdToken(LoginIdTokenPostRequest(idToken))
            }
        }

    override suspend fun postReissueToken(refreshToken: String): TuripCustomResult<ReissueTokenResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                loginService.postReissueToken(ReissueTokenRequest(refreshToken))
            }
        }

    override suspend fun getTokenVerification(accessToken: String): TuripCustomResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                loginService.getTokenVerification(AUTHORIZATION_PREFIX + accessToken)
            }
        }

    companion object {
        private const val AUTHORIZATION_PREFIX = "Bearer "
    }
}
