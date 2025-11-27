package com.on.turip.data.login.service

import com.on.turip.data.login.dto.LoginIdTokenPostRequest
import com.on.turip.data.login.dto.LoginJwtTokenResponse
import com.on.turip.data.login.dto.ReNewTokenRequest
import com.on.turip.data.login.dto.ReNewTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginService {
    @POST("login/google")
    suspend fun postIdToken(
        @Body loginIdTokenPostRequest: LoginIdTokenPostRequest,
    ): Response<LoginJwtTokenResponse>

    @POST("token")
    suspend fun postRenewToken(
        @Body reNewTokenRequest: ReNewTokenRequest,
    ): Response<ReNewTokenResponse>

    @GET("token/verification")
    suspend fun getTokenVerification(
        @Header("Authorization") token: String,
    ): Response<Unit>
}
