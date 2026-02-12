package com.on.turip.data.bookmarks.dto

import com.on.turip.data.content.dto.ContentResponse
import com.on.turip.data.content.dto.TripDurationInformationResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkContentResponse(
    @SerialName("content")
    val content: ContentResponse,
    @SerialName("tripDuration")
    val tripDuration: TripDurationInformationResponse,
    @SerialName("tripPlaceCount")
    val tripPlaceCount: Int,
)
