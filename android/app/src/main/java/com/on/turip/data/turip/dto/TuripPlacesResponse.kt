package com.on.turip.data.turip.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripPlacesResponse(
    @SerialName("turipPlaceCount")
    val turipPlaceCount: Int,
    @SerialName("turipPlaces")
    val turipPlaceResponses: List<TuripPlaceResponse>,
)
