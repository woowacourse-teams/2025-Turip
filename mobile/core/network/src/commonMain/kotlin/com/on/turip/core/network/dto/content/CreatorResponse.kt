package com.on.turip.core.network.dto.content

import kotlinx.serialization.Serializable

@Serializable
data class CreatorResponse(
    val id: Long,
    val name: String,
    val profileImageUrl: String? = null,
)
