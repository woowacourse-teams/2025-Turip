package com.on.turip.data.place.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoritePlaceOrderRequest(
    @SerialName("favoritePlaceIdsOrder")
    val favoritePlaceIdsOrder: List<Long>,
)
