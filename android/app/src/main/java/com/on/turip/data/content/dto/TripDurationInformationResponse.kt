package com.on.turip.data.content.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripDurationInformationResponse(
    @SerialName("days")
    val days: Int,
    @SerialName("nights")
    val nights: Int,
)
