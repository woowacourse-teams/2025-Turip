package com.on.turip.core.network.dto.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentInformationResponse(
    @SerialName("content")
    val content: ContentResponse,
    @SerialName("tripDuration")
    val tripDuration: TripDurationInformationResponse,
    @SerialName("tripPlaceCount")
    val tripPlaceCount: Int,
)
