package com.on.turip.core.data.dto.bookmark

import com.on.turip.core.data.dto.content.ContentResponse
import com.on.turip.core.data.dto.content.TripDurationInformationResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkContentResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("accountId")
    val accountId: Long,
    @SerialName("content")
    val content: ContentResponse,
    @SerialName("tripDuration")
    val tripDuration: TripDurationInformationResponse,
    @SerialName("tripPlaceCount")
    val tripPlaceCount: Int,
)
