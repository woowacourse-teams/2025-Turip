package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.data.datasource.FcmTokenRemoteDataSource
import com.on.turip.core.data.dto.fcm.FcmNotificationEnabledRequest
import com.on.turip.core.data.dto.fcm.FcmTokenRegisterRequest
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.service.FcmTokenService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultFcmTokenRemoteDataSource(
    private val fcmTokenService: FcmTokenService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : FcmTokenRemoteDataSource {
    override suspend fun postFcmToken(request: FcmTokenRegisterRequest): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { fcmTokenService.postFcmToken(request) }
        }

    override suspend fun patchNotificationEnabled(request: FcmNotificationEnabledRequest): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { fcmTokenService.patchNotificationEnabled(request) }
        }
}
