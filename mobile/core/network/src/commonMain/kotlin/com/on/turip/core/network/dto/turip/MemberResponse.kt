package com.on.turip.core.network.dto.turip

import kotlinx.serialization.Serializable

@Serializable
data class MemberResponse(
    val id: Long,
    val nickname: String,
    val profileImageUrl: String? = null,
)
