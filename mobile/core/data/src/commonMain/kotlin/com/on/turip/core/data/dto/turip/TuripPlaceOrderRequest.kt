package com.on.turip.core.data.dto.turip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripPlaceOrderRequest(
    @SerialName("turipPlaceIdsOrder")
    val turipPlaceIdsOrder: List<Long>,
)
