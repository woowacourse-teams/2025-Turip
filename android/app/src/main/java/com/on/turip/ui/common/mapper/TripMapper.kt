package com.on.turip.ui.common.mapper

import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.TripDuration
import com.on.turip.ui.common.model.trip.TripDurationModel
import com.on.turip.ui.common.model.trip.TripModel

fun Trip.toUiModel(): TripModel =
    TripModel(
        tripDurationModel = tripDuration.toUiModel(),
        tripPlaceCount = tripPlaceCount,
        tripCourses = tripCourses,
    )

fun TripDuration.toUiModel(): TripDurationModel =
    TripDurationModel(
        nights = nights,
        days = days,
    )
