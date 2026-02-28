package com.on.turip.data.account.service

import com.on.turip.data.account.dto.MyProfileResponse
import de.jensklingenberg.ktorfit.http.GET

interface AccountService {
    @GET("accounts/me")
    suspend fun getMyProfile(): MyProfileResponse
}
