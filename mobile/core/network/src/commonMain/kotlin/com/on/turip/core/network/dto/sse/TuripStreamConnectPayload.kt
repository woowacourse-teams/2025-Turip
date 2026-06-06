package com.on.turip.core.network.dto.sse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripStreamConnectPayload(
    @SerialName("turipId")
    val turipId: Long,
    @SerialName("timestamp")
    val timestamp: String,
)
