package com.on.turip.data.turip.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripMembersResponse(
    @SerialName("members")
    val members: List<TuripMemberResponse>,
)
