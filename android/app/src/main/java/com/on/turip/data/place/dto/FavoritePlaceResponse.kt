package com.on.turip.data.place.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoritePlaceResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("place")
    val placeResponse: PlaceResponse,
    @SerialName("favoriteOrder")
    val order: Int,
)
