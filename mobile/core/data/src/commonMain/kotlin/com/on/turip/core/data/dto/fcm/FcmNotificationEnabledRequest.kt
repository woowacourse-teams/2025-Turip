package com.on.turip.core.data.dto.fcm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmNotificationEnabledRequest(
    @SerialName("notificationEnabled")
    val notificationEnabled: Boolean,
)
