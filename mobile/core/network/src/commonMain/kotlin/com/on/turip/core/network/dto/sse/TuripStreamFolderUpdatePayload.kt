package com.on.turip.data.turip.stream

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripStreamFolderUpdatePayload(
    @SerialName("turipId")
    val turipId: Long,
    @SerialName("action")
    val action: String,
    @SerialName("timestamp")
    val timestamp: String,
)
