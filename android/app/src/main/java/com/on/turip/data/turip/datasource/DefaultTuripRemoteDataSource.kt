package com.on.turip.data.turip.datasource

import com.on.turip.BuildConfig
import com.on.turip.core.network.ApiPath
import com.on.turip.core.result.TuripResult
import com.on.turip.data.result.safeApiCall
import com.on.turip.data.turip.dto.PlaceTuripsRequest
import com.on.turip.data.turip.dto.TuripCreationResponse
import com.on.turip.data.turip.dto.TuripInvitationInformationResponse
import com.on.turip.data.turip.dto.TuripInvitationTokenResponse
import com.on.turip.data.turip.dto.TuripJoinResponse
import com.on.turip.data.turip.dto.TuripPatchRequest
import com.on.turip.data.turip.dto.TuripPlaceOrderRequest
import com.on.turip.data.turip.dto.TuripPlacesResponse
import com.on.turip.data.turip.dto.TuripPostRequest
import com.on.turip.data.turip.dto.TuripResponse
import com.on.turip.data.turip.dto.TuripsByPlaceResponse
import com.on.turip.data.turip.dto.TuripsResponse
import com.on.turip.data.turip.service.TuripService
import com.on.turip.domain.turip.TuripSseParser
import com.on.turip.domain.turip.TuripStreamEvent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultTuripRemoteDataSource @Inject constructor(
    private val turipService: TuripService,
    private val httpClient: HttpClient,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : TuripRemoteDataSource {
    override suspend fun getTurip(turipId: Long): TuripResult<TuripResponse> =
        withContext(coroutineContext) {
            safeApiCall { turipService.getTurip(turipId) }
        }

    override suspend fun getTurips(): TuripResult<TuripsResponse> =
        withContext(coroutineContext) {
            safeApiCall { turipService.getTurips() }
        }

    override suspend fun postTurip(turipPostRequest: TuripPostRequest): TuripResult<TuripCreationResponse> =
        withContext(coroutineContext) {
            safeApiCall { turipService.postTurip(turipPostRequest) }
        }

    override suspend fun patchTurip(
        turipId: Long,
        turipPatchRequest: TuripPatchRequest,
    ): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { turipService.patchTurip(turipId, turipPatchRequest) }
        }

    override suspend fun deleteTurip(turipId: Long): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { turipService.deleteTurip(turipId) }
        }

    override suspend fun exitTurip(turipId: Long): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { turipService.exitTurip(turipId) }
        }

    override suspend fun getTuripsByPlaceId(placeId: Long): TuripResult<TuripsByPlaceResponse> =
        withContext(coroutineContext) {
            safeApiCall { turipService.getTuripsByPlaceId(placeId) }
        }

    override suspend fun getTuripPlaces(turipId: Long): TuripResult<TuripPlacesResponse> =
        safeApiCall { turipService.getTuripPlaces(turipId) }

    override suspend fun createTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = safeApiCall { turipService.postTuripPlace(turipId, placeId) }

    override suspend fun deleteTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = safeApiCall { turipService.deleteTuripPlace(turipId, placeId) }

    override suspend fun patchTuripPlacesOrder(
        turipId: Long,
        turipPlaceOrderRequest: TuripPlaceOrderRequest,
    ): TuripResult<Unit> =
        safeApiCall {
            turipService.patchTuripPlaceOrder(
                turipId,
                turipPlaceOrderRequest,
            )
        }

    override suspend fun putPlaceTurips(
        placeId: Long,
        placeTuripsRequest: PlaceTuripsRequest,
    ): TuripResult<Unit> = safeApiCall { turipService.putPlaceTurips(placeId, placeTuripsRequest) }

    override suspend fun createInvitationToken(turipId: Long): TuripResult<TuripInvitationTokenResponse> =
        safeApiCall { turipService.postInvitationToken(turipId) }

    override suspend fun joinTurip(turipId: Long): TuripResult<TuripJoinResponse> = safeApiCall { turipService.postJoinTurip(turipId) }

    override suspend fun getInvitationInformation(token: String): TuripResult<TuripInvitationInformationResponse> =
        safeApiCall { turipService.getInvitationInformation(token) }

    override fun streamTuripEvents(turipId: Long): Flow<TuripResult<TuripStreamEvent>> =
        flow {
            val result =
                safeApiCall {
                    httpClient.sse(
                        urlString = "${BuildConfig.BASE_URL}${ApiPath.V1}turips/$turipId/stream",
                    ) {
                        val parser = TuripSseParser()

                        incoming.collect { event ->

                            val eventType = event.event ?: return@collect
                            val data = event.data ?: return@collect

                            val parsed =
                                parser.parse(
                                    event.id,
                                    eventType,
                                    data,
                                ) ?: return@collect

                            emit(TuripResult.Success(parsed))
                        }
                    }
                }

            if (result is TuripResult.Failure) {
                emit(result)
            }
        }.flowOn(coroutineContext)
}
