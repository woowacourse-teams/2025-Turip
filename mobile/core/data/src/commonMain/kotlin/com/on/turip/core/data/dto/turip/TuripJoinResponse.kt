package com.on.turip.core.data.dto.turip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripJoinResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("turipId")
    val turipId: Long,
    @SerialName("isShared")
    val isShared: Boolean,
    @SerialName("accountId")
    val accountId: Long,
)
