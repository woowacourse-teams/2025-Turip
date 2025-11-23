package com.on.turip.data.login.service

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.login.dto.LoginIdTokenPostRequest
import com.on.turip.data.login.dto.LoginJwtTokenResponse
import retrofit2.http.POST

interface LoginService {
    @POST
    fun postIdToken(loginIdTokenPostRequest: LoginIdTokenPostRequest): TuripCustomResult<LoginJwtTokenResponse>
}
