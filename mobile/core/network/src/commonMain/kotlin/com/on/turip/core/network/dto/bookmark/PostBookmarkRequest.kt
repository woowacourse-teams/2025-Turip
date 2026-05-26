package com.on.turip.core.network.dto.bookmark

import kotlinx.serialization.Serializable

@Serializable
data class PostBookmarkRequest(
    val contentId: Long,
)
