package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.network.dto.turip.PlaceTuripsRequest
import com.on.turip.core.network.dto.turip.TuripCreationResponse
import com.on.turip.core.network.dto.turip.TuripInvitationInformationResponse
import com.on.turip.core.network.dto.turip.TuripInvitationTokenResponse
import com.on.turip.core.network.dto.turip.TuripJoinResponse
import com.on.turip.core.network.dto.turip.TuripMembersResponse
import com.on.turip.core.network.dto.turip.TuripPatchRequest
import com.on.turip.core.network.dto.turip.TuripPlaceOrderRequest
import com.on.turip.core.network.dto.turip.TuripPlaceResponse
import com.on.turip.core.network.dto.turip.TuripPlacesResponse
import com.on.turip.core.network.dto.turip.TuripPostRequest
import com.on.turip.core.network.dto.turip.TuripResponse
import com.on.turip.core.network.dto.turip.TuripsByPlaceResponse
import com.on.turip.core.network.dto.turip.TuripsResponse
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

    @GET(ApiPath.V1 + "turips/{turipId}/members")
    suspend fun getTuripMembers(
        @Path("turipId") turipId: Long,
    ): TuripMembersResponse

    @POST(ApiPath.V1 + "turips")
    suspend fun postTurip(
        @Body body: TuripPostRequest,
    ): TuripCreationResponse

    @PATCH(ApiPath.V1 + "turips/{turipId}")
    suspend fun patchTurip(
        @Path("turipId") turipId: Long,
        @Body body: TuripPatchRequest,
    )

    @DELETE(ApiPath.V1 + "turips/{turipId}")
    suspend fun deleteTurip(
        @Path("turipId") turipId: Long,
    )

    @DELETE(ApiPath.V1 + "turips/{turipId}/exit")
    suspend fun exitTurip(
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
        @Body body: TuripPlaceOrderRequest,
    )

    @PUT(ApiPath.V1 + "turips/places/{placeId}")
    suspend fun putPlaceTurips(
        @Path("placeId") placeId: Long,
        @Body body: PlaceTuripsRequest,
    )

    @POST(ApiPath.V1 + "turips/{turipId}/invitation-tokens")
    suspend fun postInvitationToken(
        @Path("turipId") turipId: Long,
    ): TuripInvitationTokenResponse

    @POST(ApiPath.V1 + "turips/{turipId}/join")
    suspend fun postJoinTurip(
        @Path("turipId") turipId: Long,
    ): TuripJoinResponse

    @GET(ApiPath.V1 + "turips/invitation-tokens")
    suspend fun getInvitationInformation(
        @Query("token") token: String,
    ): TuripInvitationInformationResponse
}
