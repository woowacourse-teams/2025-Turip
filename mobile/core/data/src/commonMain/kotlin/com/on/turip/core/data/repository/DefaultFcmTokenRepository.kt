package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.FcmTokenRemoteDataSource
import com.on.turip.core.data.datasource.NotificationSettingLocalDataSource
import com.on.turip.core.data.dto.fcm.FcmNotificationEnabledRequest
import com.on.turip.core.data.dto.fcm.FcmTokenRegisterRequest
import com.on.turip.core.domain.repository.FcmTokenRepository
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.onSuccess

class DefaultFcmTokenRepository(
    private val fcmTokenRemoteDataSource: FcmTokenRemoteDataSource,
    private val notificationSettingLocalDataSource: NotificationSettingLocalDataSource,
) : FcmTokenRepository {
    override suspend fun registerToken(token: String): TuripResult<Unit> =
        fcmTokenRemoteDataSource.postFcmToken(FcmTokenRegisterRequest(token = token))

    override suspend fun updateNotificationEnabled(enabled: Boolean): TuripResult<Unit> =
        fcmTokenRemoteDataSource
            .patchNotificationEnabled(FcmNotificationEnabledRequest(notificationEnabled = enabled))
            .also { result ->
                result.onSuccess {
                    notificationSettingLocalDataSource.saveNotificationEnabled(enabled)
                }
            }

    override suspend fun getNotificationEnabled(): Boolean =
        notificationSettingLocalDataSource.getNotificationEnabled().getOrNull() ?: true
}
