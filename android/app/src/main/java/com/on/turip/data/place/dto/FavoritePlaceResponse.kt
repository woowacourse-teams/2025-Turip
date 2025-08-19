package com.on.turip.data.place.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoritePlaceResponse(
    @SerialName("id")
    val id: Int?,
    @SerialName("place")
    val placeResponse: PlaceResponse?,
)
