package com.on.turip.data.login.service

import com.on.turip.data.login.dto.LoginIdTokenPostRequest
import com.on.turip.data.login.dto.LoginJwtTokenResponse
import retrofit2.Response
import retrofit2.http.POST

interface LoginService {
    @POST("login/google")
    fun postIdToken(loginIdTokenPostRequest: LoginIdTokenPostRequest): Response<LoginJwtTokenResponse>
}
