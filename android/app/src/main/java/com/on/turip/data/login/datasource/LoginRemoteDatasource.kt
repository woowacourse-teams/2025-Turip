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

    override suspend fun postReissueToken(token: String): TuripCustomResult<ReissueTokenResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                loginService.postReissueToken(ReissueTokenRequest(token))
            }
        }

    override suspend fun getTokenVerification(token: String): TuripCustomResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                loginService.getTokenVerification(AUTHORIZATION_PREFIX + token)
            }
        }

    companion object {
        private const val AUTHORIZATION_PREFIX = "Bearer "
    }
}
