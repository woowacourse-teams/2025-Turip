package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.network.dto.account.MyProfileResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST

interface MemberService {
    @POST(ApiPath.V1 + "members/migration")
    suspend fun postMigration()

    @POST(ApiPath.V1 + "auth/logout")
    suspend fun postLogout()

    @DELETE(ApiPath.V1 + "members/me")
    suspend fun deleteMember()

    @POST(ApiPath.V1 + "members/migration/reject")
    suspend fun postMigrationReject()
}
