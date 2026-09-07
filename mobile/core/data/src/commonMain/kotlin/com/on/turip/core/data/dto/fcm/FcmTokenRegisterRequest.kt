package com.on.turip.core.data.dto.fcm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRegisterRequest(
    @SerialName("token")
    val token: String,
)
