package com.on.turip.data.content.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: 서버 변경되면 제거될 예정

@Serializable
data class ContentsInformationResponse2(
    @SerialName("contents")
    val contentsInformation: List<ContentInformationResponse2>,
    @SerialName("loadable")
    val loadable: Boolean,
)

@Serializable
data class ContentInformationResponse2(
    @SerialName("content")
    val content: ContentResponse2,
    @SerialName("tripDuration")
    val tripDuration: TripDurationInformationResponse,
    @SerialName("tripPlaceCount")
    val tripPlaceCount: Int,
)

@Serializable
data class ContentResponse2(
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
    val city: String,
)
