package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.data.dto.login.LoginIdTokenPostRequest
import com.on.turip.core.data.dto.login.LoginJwtTokenResponse
import com.on.turip.core.data.dto.login.ReissueTokenRequest
import com.on.turip.core.data.dto.login.ReissueTokenResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST

interface AuthService {
    @POST(ApiPath.V2 + "auth/login/google")
    suspend fun postIdToken(
        @Body loginIdTokenPostRequest: LoginIdTokenPostRequest,
    ): LoginJwtTokenResponse

    @POST(ApiPath.V1 + "auth/login/apple")
    suspend fun postAppleIdToken(
        @Body loginIdTokenPostRequest: LoginIdTokenPostRequest,
    ): LoginJwtTokenResponse

    @POST(ApiPath.V1 + "auth/tokens")
    suspend fun postReissueToken(
        @Body reissueTokenRequest: ReissueTokenRequest,
    ): ReissueTokenResponse

    @GET(ApiPath.V1 + "auth/tokens/verification")
    suspend fun verifyToken()
}
