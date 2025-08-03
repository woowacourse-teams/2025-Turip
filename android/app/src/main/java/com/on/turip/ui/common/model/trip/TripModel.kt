package com.on.turip.ui.common.model.trip

import com.on.turip.domain.trip.TripCourse

data class TripModel(
    val tripDurationModel: TripDurationModel,
    val tripPlaceCount: Int,
    val tripCourses: List<TripCourse> = emptyList<TripCourse>(),
)
