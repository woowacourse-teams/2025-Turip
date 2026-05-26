package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.network.dto.auth.LoginIdTokenPostRequest
import com.on.turip.core.network.dto.auth.LoginJwtTokenResponse
import com.on.turip.core.network.dto.auth.ReissueTokenRequest
import com.on.turip.core.network.dto.auth.ReissueTokenResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST

interface AuthService {
    @POST(ApiPath.V1 + "auth/login")
    suspend fun postLogin(
        @Body body: LoginIdTokenPostRequest,
    ): LoginJwtTokenResponse

    @POST(ApiPath.V1 + "auth/guest")
    suspend fun postGuestLogin(): LoginJwtTokenResponse

    @POST(ApiPath.V2 + "auth/reissue")
    suspend fun postReissueToken(
        @Body body: ReissueTokenRequest,
    ): ReissueTokenResponse

    @GET(ApiPath.V1 + "auth/tokens/verification")
    suspend fun verifyToken()
}
