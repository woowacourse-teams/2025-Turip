package com.on.turip.core.data.datasource

import com.on.turip.core.data.dto.fcm.FcmNotificationEnabledRequest
import com.on.turip.core.data.dto.fcm.FcmTokenRegisterRequest
import com.on.turip.core.model.result.TuripResult

interface FcmTokenRemoteDataSource {
    suspend fun postFcmToken(request: FcmTokenRegisterRequest): TuripResult<Unit>

    suspend fun patchNotificationEnabled(request: FcmNotificationEnabledRequest): TuripResult<Unit>
}
