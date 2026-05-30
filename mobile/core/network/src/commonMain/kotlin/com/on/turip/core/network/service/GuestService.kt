package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.network.dto.login.LoginJwtTokenResponse
import de.jensklingenberg.ktorfit.http.POST

interface GuestService {
    @POST(ApiPath.V1 + "auth/guest")
    suspend fun postGuestLogin(): LoginJwtTokenResponse
}
