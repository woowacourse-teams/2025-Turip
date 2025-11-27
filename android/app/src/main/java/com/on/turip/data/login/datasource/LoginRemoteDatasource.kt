package com.on.turip.data.login.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.login.dto.LoginIdTokenPostRequest
import com.on.turip.data.login.dto.LoginJwtTokenResponse
import com.on.turip.data.login.dto.ReNewTokenRequest
import com.on.turip.data.login.dto.ReNewTokenResponse
import com.on.turip.data.login.service.LoginService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class LoginRemoteDatasource @Inject constructor(
    val loginService: LoginService,
    val coroutineContext: CoroutineContext = Dispatchers.IO,
) : LoginDatasource {
    override suspend fun postIdToken(idToken: String): TuripCustomResult<LoginJwtTokenResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                loginService.postIdToken(LoginIdTokenPostRequest(idToken))
            }
        }

    override suspend fun postReNewToken(token: String): TuripCustomResult<ReNewTokenResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                loginService.postRenewToken(ReNewTokenRequest(token))
            }
        }
}
