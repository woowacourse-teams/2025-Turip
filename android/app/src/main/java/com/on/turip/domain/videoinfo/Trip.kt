package com.on.turip.domain.videoinfo

import com.on.turip.domain.videoinfo.contents.TripDuration

data class Trip(
    val tripDuration: TripDuration,
    val tripPlaceCount: Int,
    val tripCourses: List<TripCourse> = emptyList<TripCourse>(),
)
