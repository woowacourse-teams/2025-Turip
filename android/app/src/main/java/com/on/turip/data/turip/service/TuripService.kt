package com.on.turip.data.turip.service

import com.on.turip.core.network.ApiPath
import com.on.turip.data.turip.dto.PlaceTuripsRequest
import com.on.turip.data.turip.dto.TuripCreationResponse
import com.on.turip.data.turip.dto.TuripInvitationInformationResponse
import com.on.turip.data.turip.dto.TuripInvitationTokenResponse
import com.on.turip.data.turip.dto.TuripPatchRequest
import com.on.turip.data.turip.dto.TuripPlaceOrderRequest
import com.on.turip.data.turip.dto.TuripPlacesResponse
import com.on.turip.data.turip.dto.TuripPostRequest
import com.on.turip.data.turip.dto.TuripResponse
import com.on.turip.data.turip.dto.TuripsByPlaceResponse
import com.on.turip.data.turip.dto.TuripsResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface TuripService {
    @GET(ApiPath.V1 + "turips/{turipId}")
    suspend fun getTurip(
        @Path("turipId") turipId: Long,
    ): TuripResponse

    @GET(ApiPath.V1 + "turips")
    suspend fun getTurips(): TuripsResponse

    @POST(ApiPath.V1 + "turips")
    suspend fun postTurip(
        @Body turipPostRequest: TuripPostRequest,
    ): TuripCreationResponse

    @PATCH(ApiPath.V1 + "turips/{turipId}")
    suspend fun patchTurip(
        @Path("turipId") turipId: Long,
        @Body turipPatchRequest: TuripPatchRequest,
    )

    @DELETE(ApiPath.V1 + "turips/{turipId}")
    suspend fun deleteTurip(
        @Path("turipId") turipId: Long,
    )

    @GET(ApiPath.V1 + "turips/turip-status")
    suspend fun getTuripsByPlaceId(
        @Query("placeId") placeId: Long,
    ): TuripsByPlaceResponse

    @GET(ApiPath.V1 + "turips/places")
    suspend fun getTuripPlaces(
        @Query("turipId") turipId: Long,
    ): TuripPlacesResponse

    @POST(ApiPath.V1 + "turips/places")
    suspend fun postTuripPlace(
        @Query("turipId") turipId: Long,
        @Query("placeId") placeId: Long,
    )

    @DELETE(ApiPath.V1 + "turips/places")
    suspend fun deleteTuripPlace(
        @Query("turipId") turipId: Long,
        @Query("placeId") placeId: Long,
    )

    @PATCH(ApiPath.V1 + "turips/places/turip-order")
    suspend fun patchTuripPlaceOrder(
        @Query("turipId") turipId: Long,
        @Body turipPlaceOrderRequest: TuripPlaceOrderRequest,
    )

    @PUT(ApiPath.V1 + "turips/places/{placeId}")
    suspend fun putPlaceTurips(
        @Path("placeId") placeId: Long,
        @Body placeTuripsRequest: PlaceTuripsRequest,
    )

    @POST(ApiPath.V1 + "turips/{turipId}/invitation-tokens")
    suspend fun postInvitationToken(
        @Path("turipId") turipId: Long,
    ): TuripInvitationTokenResponse

    @GET(ApiPath.V1 + "turips/invitation-tokens")
    suspend fun getInvitationInformation(
        @Query("token") token: String,
    ): TuripInvitationInformationResponse
}
