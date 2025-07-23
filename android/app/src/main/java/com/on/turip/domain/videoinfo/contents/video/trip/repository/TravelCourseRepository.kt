package com.on.turip.domain.videoinfo.contents.video.trip.repository

import com.on.turip.domain.videoinfo.contents.video.trip.Trip

interface TravelCourseRepository {
    fun loadTripInfo(contentId: Long): Result<Trip>
}
