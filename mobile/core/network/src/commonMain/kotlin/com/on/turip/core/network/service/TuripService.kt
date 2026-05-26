package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.network.dto.turip.AddTuripPlaceRequest
import com.on.turip.core.network.dto.turip.CreateTuripRequest
import com.on.turip.core.network.dto.turip.InvitationInformationResponse
import com.on.turip.core.network.dto.turip.InvitationTokenResponse
import com.on.turip.core.network.dto.turip.MemberResponse
import com.on.turip.core.network.dto.turip.RenameTuripRequest
import com.on.turip.core.network.dto.turip.ReorderTuripPlacesRequest
import com.on.turip.core.network.dto.turip.TuripPlaceResponse
import com.on.turip.core.network.dto.turip.TuripResponse
import com.on.turip.core.network.dto.turip.TuripStatusResponse
import com.on.turip.core.network.dto.turip.UpdatePlaceTuripsRequest
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
    suspend fun getTurip(@Path("turipId") turipId: Long): TuripResponse

    @GET(ApiPath.V1 + "turips")
    suspend fun getTurips(): List<TuripResponse>

    @GET(ApiPath.V1 + "turips/{turipId}/members")
    suspend fun getTuripMembers(@Path("turipId") turipId: Long): List<MemberResponse>

    @POST(ApiPath.V1 + "turips")
    suspend fun postTurip(@Body body: CreateTuripRequest): TuripResponse

    @PATCH(ApiPath.V1 + "turips/{turipId}")
    suspend fun patchTurip(
        @Path("turipId") turipId: Long,
        @Body body: RenameTuripRequest,
    )

    @DELETE(ApiPath.V1 + "turips/{turipId}")
    suspend fun deleteTurip(@Path("turipId") turipId: Long)

    @DELETE(ApiPath.V1 + "turips/{turipId}/exit")
    suspend fun exitTurip(@Path("turipId") turipId: Long)

    @GET(ApiPath.V1 + "turips/turip-status")
    suspend fun getTuripsByPlaceId(@Query("placeId") placeId: Long): List<TuripStatusResponse>

    @GET(ApiPath.V1 + "turips/places")
    suspend fun getTuripPlaces(@Query("turipId") turipId: Long): List<TuripPlaceResponse>

    @POST(ApiPath.V1 + "turips/places")
    suspend fun postTuripPlace(@Body body: AddTuripPlaceRequest)

    @DELETE(ApiPath.V1 + "turips/places")
    suspend fun deleteTuripPlace(
        @Query("turipId") turipId: Long,
        @Query("placeId") placeId: Long,
    )

    @PATCH(ApiPath.V1 + "turips/places/turip-order")
    suspend fun patchTuripPlaceOrder(@Body body: ReorderTuripPlacesRequest)

    @PUT(ApiPath.V1 + "turips/places/{placeId}")
    suspend fun putPlaceTurips(
        @Path("placeId") placeId: Long,
        @Body body: UpdatePlaceTuripsRequest,
    )

    @POST(ApiPath.V1 + "turips/{turipId}/invitation-tokens")
    suspend fun postInvitationToken(@Path("turipId") turipId: Long): InvitationTokenResponse

    @POST(ApiPath.V1 + "turips/{turipId}/join")
    suspend fun postJoinTurip(@Path("turipId") turipId: Long)

    @GET(ApiPath.V1 + "turips/invitation-tokens")
    suspend fun getInvitationInformation(@Query("token") token: String): InvitationInformationResponse
}
