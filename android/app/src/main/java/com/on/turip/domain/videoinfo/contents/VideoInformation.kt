package com.on.turip.domain.videoinfo.contents

import com.on.turip.domain.videoinfo.TripCourse

data class VideoInformation(
    val content: Content,
    val tripDuration: TripDuration,
    val tripPlaceCount: Int,
    val tripCourses: List<TripCourse> = emptyList<TripCourse>(),
)
