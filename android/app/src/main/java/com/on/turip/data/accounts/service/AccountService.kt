package com.on.turip.data.accounts.service

import com.on.turip.data.accounts.dto.MyProfileResponse
import de.jensklingenberg.ktorfit.http.GET

interface AccountService {
    @GET("accounts/me")
    suspend fun getMyProfile(): MyProfileResponse
}
