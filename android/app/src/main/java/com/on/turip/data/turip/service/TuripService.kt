package com.on.turip.data.turip.service

import com.on.turip.data.turip.dto.TuripCreationResponse
import com.on.turip.data.turip.dto.TuripPatchRequest
import com.on.turip.data.turip.dto.TuripPostRequest
import com.on.turip.data.turip.dto.TuripsByPlaceResponse
import com.on.turip.data.turip.dto.TuripsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TuripService {
    @GET("turips")
    suspend fun getTurips(): Response<TuripsResponse>

    @POST("turips")
    suspend fun postTurip(
        @Body turipPostRequest: TuripPostRequest,
    ): Response<TuripCreationResponse>

    @PATCH("turips/{turipId}")
    suspend fun patchTurip(
        @Path("turipId") turipId: Long,
        @Body turipPatchRequest: TuripPatchRequest,
    ): Response<Unit>

    @DELETE("turips/{turipId}")
    suspend fun deleteTurip(
        @Path("turipId") turipId: Long,
    ): Response<Unit>

    @GET("turips/turip-status")
    suspend fun getTuripsByPlaceId(
        @Query("placeId") placeId: Long,
    ): Response<TuripsByPlaceResponse>
}
