package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.network.dto.account.AccountResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET

interface MemberService {
    @GET(ApiPath.V1 + "members/me")
    suspend fun getMember(): AccountResponse

    @DELETE(ApiPath.V1 + "members/me")
    suspend fun deleteMember()
}
