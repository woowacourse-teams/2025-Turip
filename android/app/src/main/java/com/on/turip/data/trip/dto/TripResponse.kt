package com.on.turip.data.trip.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripResponse(
    @SerialName("tripCourses")
    val tripCourses: List<TripCourseResponse>,
    @SerialName("tripDuration")
    val tripDuration: TripDurationResponse,
    @SerialName("tripPlaceCount")
    val tripPlaceCount: Int,
)
