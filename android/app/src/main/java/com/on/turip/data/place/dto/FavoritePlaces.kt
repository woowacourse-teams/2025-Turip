package com.on.turip.data.place.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoritePlaces(
    @SerialName("favoritePlaceCount")
    val favoritePlaceCount: Int?,
    @SerialName("favoritePlaces")
    val favoritePlaces: List<FavoritePlace?>?,
)
