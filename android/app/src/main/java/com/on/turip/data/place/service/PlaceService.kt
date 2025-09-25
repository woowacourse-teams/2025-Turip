package com.on.turip.data.place.service

import com.on.turip.data.place.dto.FavoritePlaceOrderRequest
import com.on.turip.data.place.dto.FavoritePlacesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface PlaceService {
    @GET("favorites/places")
    suspend fun getFavoritePlaces(
        @Query("favoriteFolderId") favoriteFolderId: Long,
    ): Response<FavoritePlacesResponse>

    @POST("favorites/places")
    suspend fun postFavoritePlace(
        @Query("favoriteFolderId") favoriteFolderId: Long,
        @Query("placeId") placeId: Long,
    ): Response<Unit>

    @DELETE("favorites/places")
    suspend fun deleteFavoritePlace(
        @Query("favoriteFolderId") favoriteFolderId: Long,
        @Query("placeId") placeId: Long,
    ): Response<Unit>

    @PATCH("favorites/places/favorite-order")
    suspend fun patchFavoritePlaceOrder(
        @Query("favoriteFolderId") favoriteFolderId: Long,
        @Body favoritePlaceOrderRequest: FavoritePlaceOrderRequest,
    ): Response<Unit>
}
