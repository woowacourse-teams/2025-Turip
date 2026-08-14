package com.on.turip.core.network.service

import com.on.turip.core.data.dto.fcm.FcmNotificationEnabledRequest
import com.on.turip.core.data.dto.fcm.FcmTokenRegisterRequest
import com.on.turip.core.network.ApiPath
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST

interface FcmTokenService {
    @POST(ApiPath.V1 + "fcm-tokens")
    suspend fun postFcmToken(
        @Body request: FcmTokenRegisterRequest,
    )

    @PATCH(ApiPath.V1 + "fcm-tokens/notification")
    suspend fun patchNotificationEnabled(
        @Body request: FcmNotificationEnabledRequest,
    )
}
