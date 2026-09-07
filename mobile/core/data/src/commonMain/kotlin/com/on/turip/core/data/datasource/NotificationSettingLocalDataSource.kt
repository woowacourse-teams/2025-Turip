package com.on.turip.core.data.datasource

interface NotificationSettingLocalDataSource {
    suspend fun saveNotificationEnabled(enabled: Boolean): Result<Unit>

    suspend fun getNotificationEnabled(): Result<Boolean?>
}
