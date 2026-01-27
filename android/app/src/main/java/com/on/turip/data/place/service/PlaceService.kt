package com.on.turip.data.place.service

import com.on.turip.data.place.dto.TuripPlaceOrderRequest
import com.on.turip.data.place.dto.TuripPlacesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface PlaceService {
    @GET("turips/places")
    suspend fun getTuripPlaces(
        @Query("turipId") turipId: Long,
    ): Response<TuripPlacesResponse>

    @POST("turips/places")
    suspend fun postTuripPlace(
        @Query("turipId") turipId: Long,
        @Query("placeId") placeId: Long,
    ): Response<Unit>

    @DELETE("turips/places")
    suspend fun deleteTuripPlace(
        @Query("turipId") turipId: Long,
        @Query("placeId") placeId: Long,
    ): Response<Unit>

    @PATCH("turips/places/turip-place-order")
    suspend fun patchTuripPlaceOrder(
        @Query("turipId") turipId: Long,
        @Body turipPlaceOrderRequest: TuripPlaceOrderRequest,
    ): Response<Unit>
}
