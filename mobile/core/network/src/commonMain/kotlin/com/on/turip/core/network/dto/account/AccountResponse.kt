package com.on.turip.core.network.dto.account

import kotlinx.serialization.Serializable

@Serializable
data class AccountResponse(
    val id: Long,
    val nickname: String,
    val profileImageUrl: String? = null,
)
