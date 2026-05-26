package com.on.turip.core.network.dto.content

import kotlinx.serialization.Serializable

@Serializable
data class UsersLikeContentResponse(
    val content: ContentResponse,
    val tripDuration: String,
)
