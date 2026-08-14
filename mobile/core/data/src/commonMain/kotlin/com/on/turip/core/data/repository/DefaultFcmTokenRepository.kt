package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.FcmTokenRemoteDataSource
import com.on.turip.core.data.datasource.NotificationSettingLocalDataSource
import com.on.turip.core.data.dto.fcm.FcmNotificationEnabledRequest
import com.on.turip.core.data.dto.fcm.FcmTokenRegisterRequest
import com.on.turip.core.domain.repository.FcmTokenRepository
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.onSuccess

private const val DEFAULT_NOTIFICATION_ENABLED = true

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

    /**
     * 저장된 값이 없는 것(최초 진입)과 조회 실패는 다르다.
     * 전자만 기본값으로 채우고, 후자는 실패를 그대로 호출자에게 전달한다.
     */
    override suspend fun getNotificationEnabled(): TuripResult<Boolean> =
        notificationSettingLocalDataSource
            .getNotificationEnabled()
            .fold(
                onSuccess = { enabled -> TuripResult.Success(enabled ?: DEFAULT_NOTIFICATION_ENABLED) },
                onFailure = { cause -> TuripResult.Failure(ErrorType.Unknown, cause) },
            )
}
