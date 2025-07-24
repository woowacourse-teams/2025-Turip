package com.on.turip.data.trip.service

import com.on.turip.data.trip.dto.TripResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TripService {
    @GET("trip-courses")
    suspend fun getTrip(
        @Query("contentId") contentId: Long,
    ): TripResponse
}
