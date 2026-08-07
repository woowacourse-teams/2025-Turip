package com.on.turip.core.data.dto.turip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripPlaceResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("place")
    val placeResponse: PlaceResponse,
    @SerialName("turipPlaceOrder")
    val order: Long,
)
