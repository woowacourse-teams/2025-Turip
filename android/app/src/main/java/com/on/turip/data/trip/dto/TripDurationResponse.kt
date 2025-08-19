package com.on.turip.data.trip.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripDurationResponse(
    @SerialName("days")
    val days: Int,
    @SerialName("nights")
    val nights: Int,
)
