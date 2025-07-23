package com.on.turip.domain.videoinfo

interface TravelCourseRepository {
    fun loadTripInfo(contentId: Long): Result<Trip>
}
