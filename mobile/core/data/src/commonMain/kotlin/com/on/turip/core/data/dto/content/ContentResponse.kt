package com.on.turip.core.data.dto.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentResponse(
    @SerialName("creator")
    val creator: CreatorInformationResponse,
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("uploadedDate")
    val uploadedDate: String,
    @SerialName("url")
    val url: String,
    @SerialName("city")
    val city: CityResponse,
    @SerialName("isBookmarked")
    val isBookmarked: Boolean,
)
