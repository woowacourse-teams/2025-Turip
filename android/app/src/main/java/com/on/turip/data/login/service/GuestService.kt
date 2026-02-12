package com.on.turip.data.login.service

import de.jensklingenberg.ktorfit.http.DELETE

interface GuestService {
    @DELETE("guests/me")
    suspend fun deleteGuest()
}
