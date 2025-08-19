package com.on.turip.data.place.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoritePlace(
    @SerialName("id")
    val id: Int?,
    @SerialName("place")
    val place: Place?
)