package com.on.turip.data.login.service

import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.POST

interface MemberService {
    @POST("members/migration")
    suspend fun postMigration()

    @POST("auth/logout")
    suspend fun postLogout()

    @DELETE("members/me")
    suspend fun deleteMember()
}
