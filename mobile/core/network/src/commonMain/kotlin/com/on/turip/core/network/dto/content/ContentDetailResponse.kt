package com.on.turip.core.network.dto.content

import com.on.turip.core.network.dto.creator.CreatorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentDetailResponse(
    @SerialName("city")
    val city: CityResponse,
    @SerialName("creator")
    val creator: CreatorResponse,
    @SerialName("id")
    val id: Long,
    @SerialName("isBookmarked")
    val isBookmarked: Boolean,
    @SerialName("title")
    val title: String,
    @SerialName("uploadedDate")
    val uploadedDate: String,
    @SerialName("url")
    val url: String,
)
