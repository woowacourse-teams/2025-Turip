package com.on.turip.data.login.service

import retrofit2.Response
import retrofit2.http.POST

interface AuthService {
    @POST("members/migration")
    suspend fun postMigration(): Response<Unit>

    @POST("logout")
    suspend fun postLogout(): Response<Unit>

    @POST("members/me")
    suspend fun postWithdraw(): Response<Unit>
}
