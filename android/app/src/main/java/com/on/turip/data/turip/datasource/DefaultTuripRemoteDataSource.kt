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
import com.on.turip.data.turip.dto.TuripMembersResponse
import com.on.turip.data.turip.dto.TuripPatchRequest
import com.on.turip.data.turip.dto.TuripPlaceOrderRequest
import com.on.turip.data.turip.dto.TuripPlacesResponse
import com.on.turip.data.turip.dto.TuripPostRequest
import com.on.turip.data.turip.dto.TuripResponse
import com.on.turip.data.turip.dto.TuripsByPlaceResponse
import com.on.turip.data.turip.dto.TuripsResponse
import com.on.turip.data.turip.service.TuripService
import com.on.turip.di.SseHttpClient
import com.on.turip.domain.turip.TuripSseParser
import com.on.turip.domain.turip.TuripStreamEvent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultTuripRemoteDataSource @Inject constructor(
    private val turipService: TuripService,
    @SseHttpClient private val httpClient: HttpClient,
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

    override suspend fun getTuripMembers(turipId: Long): TuripResult<TuripMembersResponse> =
        withContext(coroutineContext) {
            safeApiCall { turipService.getTuripMembers(turipId) }
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
            val result: TuripResult<Unit> =
                safeApiCall {
                    httpClient.sse(
                        urlString = "${BuildConfig.BASE_URL}${ApiPath.V1}turips/$turipId/stream",
                    ) {
                        val parser = TuripSseParser()

                        incoming.collect { event: ServerSentEvent ->
                            val eventType: String =
                                event.event ?: run {
                                    Timber.w(
                                        "튜립 SSE 이벤트 타입 누락. id=%s, dataLen=%s",
                                        event.id,
                                        event.data?.length,
                                    )
                                    return@collect
                                }
                            val data: String =
                                event.data ?: run {
                                    Timber.w(
                                        "튜립 SSE 이벤트 데이터 누락. id=%s, type=%s",
                                        event.id,
                                        eventType,
                                    )
                                    return@collect
                                }

                            val parsed: TuripStreamEvent =
                                parser.parse(
                                    event.id,
                                    eventType,
                                    data,
                                ) ?: run {
                                    Timber.w(
                                        "튜립 SSE 이벤트 파싱 실패. id=%s, type=%s, dataLen=%s",
                                        event.id,
                                        eventType,
                                        data.length,
                                    )
                                    return@collect
                                }

                            emit(TuripResult.Success(parsed))
                        }
                    }
                }

            if (result is TuripResult.Failure) {
                emit(TuripResult.Failure(errorType = result.errorType, cause = result.cause))
            }
        }.flowOn(coroutineContext)
}
