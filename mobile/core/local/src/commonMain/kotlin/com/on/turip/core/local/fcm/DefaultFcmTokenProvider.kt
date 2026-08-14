package com.on.turip.core.local.fcm

import com.on.turip.core.domain.fcm.FcmTokenProvider

class DefaultFcmTokenProvider : FcmTokenProvider {
    override suspend fun fetchToken(): String? = fetchPlatformFcmToken()
}
