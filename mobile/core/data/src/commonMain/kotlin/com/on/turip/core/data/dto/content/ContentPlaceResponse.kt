package com.on.turip.core.data.dto.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentPlaceResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("place")
    val place: PlaceResponse,
    @SerialName("visitDay")
    val visitDay: Int,
    @SerialName("visitOrder")
    val visitOrder: Int,
    @SerialName("timeLine")
    val timeLine: String,
    @SerialName("isTuripPlace")
    val isTuripPlace: Boolean,
)
