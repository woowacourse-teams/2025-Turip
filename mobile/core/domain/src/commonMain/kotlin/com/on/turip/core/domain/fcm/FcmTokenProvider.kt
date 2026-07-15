package com.on.turip.core.domain.fcm

interface FcmTokenProvider {
    suspend fun fetchToken(): String?
}
