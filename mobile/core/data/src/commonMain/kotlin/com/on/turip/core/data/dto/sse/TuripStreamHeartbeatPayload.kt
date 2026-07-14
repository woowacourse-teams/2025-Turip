package com.on.turip.data.turip.stream

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripStreamHeartbeatPayload(
    @SerialName("timestamp")
    val timestamp: String,
)
