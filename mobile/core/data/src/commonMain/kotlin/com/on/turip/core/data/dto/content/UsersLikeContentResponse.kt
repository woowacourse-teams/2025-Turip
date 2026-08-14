package com.on.turip.core.data.dto.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersLikeContentResponse(
    @SerialName("content")
    val content: ContentResponse,
    @SerialName("tripDuration")
    val tripDuration: TripDurationInformationResponse,
)
