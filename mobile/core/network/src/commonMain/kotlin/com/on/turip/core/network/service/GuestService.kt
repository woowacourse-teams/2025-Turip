package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.data.dto.login.LoginJwtTokenResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.POST

interface GuestService {
    @DELETE(ApiPath.V1 + "guests/me")
    suspend fun deleteGuest()
}
