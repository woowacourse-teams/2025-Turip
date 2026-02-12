package com.on.turip.data.turip.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripsByPlaceResponse(
    @SerialName("turips")
    val turips: List<TuripByPlaceResponse>,
)
