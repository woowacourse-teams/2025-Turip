package com.on.turip.data.place.service

import com.on.turip.data.place.dto.FavoritePlacesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    @GET("favorite-places")
    suspend fun getFavoritePlaces(
        @Query("favoriteFolderId") favoriteFolderId: Long,
    ): Response<FavoritePlacesResponse>
}
