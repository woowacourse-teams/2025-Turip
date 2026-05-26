package com.on.turip.core.network.dto.content

import kotlinx.serialization.Serializable

@Serializable
data class ContentResponse(
    val id: Long,
    val title: String,
    val description: String,
    val creator: CreatorResponse,
    val videoData: VideoDataResponse,
    val places: List<PlaceResponse>,
    val isBookmarked: Boolean,
)
