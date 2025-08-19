package com.on.turip.data.place.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoritePlaceRequest(
    @SerialName("favoriteFolderId")
    val favoriteFolderId: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("placeId")
    val placeId: Int,
)
