package com.on.turip.data.turip.service

import com.on.turip.data.turip.dto.TuripCreationResponse
import com.on.turip.data.turip.dto.TuripPatchRequest
import com.on.turip.data.turip.dto.TuripPlaceOrderRequest
import com.on.turip.data.turip.dto.TuripPlacesResponse
import com.on.turip.data.turip.dto.TuripPostRequest
import com.on.turip.data.turip.dto.TuripsByPlaceResponse
import com.on.turip.data.turip.dto.TuripsResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface TuripService {
    @GET("turips")
    suspend fun getTurips(): TuripsResponse

    @POST("turips")
    suspend fun postTurip(
        @Body turipPostRequest: TuripPostRequest,
    ): TuripCreationResponse

    @PATCH("turips/{turipId}")
    suspend fun patchTurip(
        @Path("turipId") turipId: Long,
        @Body turipPatchRequest: TuripPatchRequest,
    )

    @DELETE("turips/{turipId}")
    suspend fun deleteTurip(
        @Path("turipId") turipId: Long,
    )

    @GET("turips/turip-status")
    suspend fun getTuripsByPlaceId(
        @Query("placeId") placeId: Long,
    ): TuripsByPlaceResponse

    @GET("turips/places")
    suspend fun getTuripPlaces(
        @Query("turipId") turipId: Long,
    ): TuripPlacesResponse

    @POST("turips/places")
    suspend fun postTuripPlace(
        @Query("turipId") turipId: Long,
        @Query("placeId") placeId: Long,
    )

    @DELETE("turips/places")
    suspend fun deleteTuripPlace(
        @Query("turipId") turipId: Long,
        @Query("placeId") placeId: Long,
    )

    @PATCH("turips/places/turip-order")
    suspend fun patchTuripPlaceOrder(
        @Query("turipId") turipId: Long,
        @Body turipPlaceOrderRequest: TuripPlaceOrderRequest,
    )
}
