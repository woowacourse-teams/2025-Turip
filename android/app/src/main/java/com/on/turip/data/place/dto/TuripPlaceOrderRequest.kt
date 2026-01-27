package com.on.turip.data.place.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripPlaceOrderRequest(
    @SerialName("turipPlaceIdsOrder")
    val turipPlaceIdsOrder: List<Long>,
)
