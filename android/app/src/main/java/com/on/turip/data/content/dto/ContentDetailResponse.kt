package com.on.turip.data.content.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentDetailResponse(
    @SerialName("city")
    val city: City,
    @SerialName("creator")
    val creator: Creator,
    @SerialName("id")
    val id: Int,
    @SerialName("isFavorite")
    val isFavorite: Boolean,
    @SerialName("title")
    val title: String,
    @SerialName("uploadedDate")
    val uploadedDate: String,
    @SerialName("url")
    val url: String,
)
