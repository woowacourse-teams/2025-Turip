package com.on.turip.core.network.dto.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripDurationResponse(
    @SerialName("days")
    val days: Int,
    @SerialName("nights")
    val nights: Int,
)
