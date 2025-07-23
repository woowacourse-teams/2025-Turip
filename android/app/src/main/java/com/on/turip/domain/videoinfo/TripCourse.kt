package com.on.turip.domain.videoinfo

data class TripCourse(
    val tripCourseId: Long,
    val visitDay: Int,
    val visitOrder: Int,
    val place: Place,
)
