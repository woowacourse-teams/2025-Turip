package com.on.turip.data.bookmark.dto

import com.on.turip.data.content.dto.ContentResponse
import com.on.turip.data.content.dto.TripDurationInformationResponse
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
