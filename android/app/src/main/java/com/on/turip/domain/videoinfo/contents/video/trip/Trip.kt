package com.on.turip.domain.videoinfo.contents.video.trip

data class Trip(
    val tripDuration: TripDuration,
    val tripPlaceCount: Int,
    val tripCourses: List<TripCourse> = emptyList<TripCourse>(),
)
