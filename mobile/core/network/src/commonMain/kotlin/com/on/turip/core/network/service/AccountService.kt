package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.network.dto.account.MyProfileResponse
import de.jensklingenberg.ktorfit.http.GET

interface AccountService {
    @GET(ApiPath.V1 + "accounts/me")
    suspend fun getMyProfile(): MyProfileResponse
}
