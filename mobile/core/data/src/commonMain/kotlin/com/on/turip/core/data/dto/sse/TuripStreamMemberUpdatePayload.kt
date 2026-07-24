package com.on.turip.data.turip.stream

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripStreamMemberUpdatePayload(
    @SerialName("turipId")
    val turipId: Long,
    @SerialName("action")
    val action: String,
    @SerialName("memberCount")
    val memberCount: Int = 0,
    @SerialName("members")
    val members: List<TuripStreamMemberPayload> = emptyList(),
    @SerialName("timestamp")
    val timestamp: String,
)
