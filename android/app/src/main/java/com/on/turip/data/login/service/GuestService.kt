package com.on.turip.data.login.service

import retrofit2.Response
import retrofit2.http.DELETE

interface GuestService {
    @DELETE("guests/me")
    suspend fun deleteGuest(): Response<Unit>
}
