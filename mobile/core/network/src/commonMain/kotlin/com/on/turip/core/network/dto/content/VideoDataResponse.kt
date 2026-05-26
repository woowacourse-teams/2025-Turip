package com.on.turip.core.network.dto.content

import kotlinx.serialization.Serializable

@Serializable
data class VideoDataResponse(
    val videoUrl: String,
    val thumbnailUrl: String? = null,
)
