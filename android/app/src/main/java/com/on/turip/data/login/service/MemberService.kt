package com.on.turip.data.login.service

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST

interface MemberService {
    @POST("members/migration")
    suspend fun postMigration(): Response<Unit>

    @POST("logout")
    suspend fun postLogout(): Response<Unit>

    @DELETE("members/me")
    suspend fun postWithdraw(): Response<Unit>
}
