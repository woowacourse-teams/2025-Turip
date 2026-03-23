package com.on.turip.data.turip.stream

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripStreamConnectPayload(
    @SerialName("turipId")
    val turipId: Long,
    @SerialName("timestamp")
    val timestamp: String,
)
