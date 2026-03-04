package com.on.turip.data.login.service

import com.on.turip.core.network.ApiPath
import de.jensklingenberg.ktorfit.http.DELETE

interface GuestService {
    @DELETE(ApiPath.V1 + "guests/me")
    suspend fun deleteGuest()
}
