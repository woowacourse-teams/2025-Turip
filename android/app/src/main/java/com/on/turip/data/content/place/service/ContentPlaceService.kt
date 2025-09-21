package com.on.turip.data.content.place.service

import com.on.turip.data.content.place.dto.ContentPlacesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ContentPlaceService {
    @GET("contents/{contentId}/places")
    suspend fun getTrip(
        @Path("contentId") contentId: Long,
    ): Response<ContentPlacesResponse>
}
