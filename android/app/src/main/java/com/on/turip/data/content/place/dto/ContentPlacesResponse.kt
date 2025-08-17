package com.on.turip.data.content.place.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentPlacesResponse(
    @SerialName("contentPlaces")
    val contentPlaces: List<ContentPlaceResponse>,
    @SerialName("tripDuration")
    val tripDuration: TripDurationResponse,
    @SerialName("contentPlaceCount")
    val contentPlaceCount: Int,
)
