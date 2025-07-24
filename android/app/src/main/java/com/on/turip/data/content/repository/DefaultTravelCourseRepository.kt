package com.on.turip.data.content.repository

import com.on.turip.domain.videoinfo.contents.video.trip.Trip
import com.on.turip.domain.videoinfo.contents.video.trip.repository.TravelCourseRepository

class DefaultTravelCourseRepository : TravelCourseRepository {
    override fun loadTripInfo(contentId: Long): Result<Trip> {
        TODO("Not yet implemented")
    }
}
