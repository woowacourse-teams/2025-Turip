package com.on.turip.domain.videoinfo.contents.video.trip

data class TripCourse(
    val tripCourseId: Long,
    val visitDay: Int,
    val visitOrder: Int,
    val place: Place,
)
