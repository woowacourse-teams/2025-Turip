package com.on.turip.core.network.service

import com.on.turip.core.data.dto.account.MyProfileResponse
import com.on.turip.core.network.ApiPath
import de.jensklingenberg.ktorfit.http.GET

interface AccountService {
    @GET(ApiPath.V1 + "accounts/me")
    suspend fun getMyProfile(): MyProfileResponse
}
