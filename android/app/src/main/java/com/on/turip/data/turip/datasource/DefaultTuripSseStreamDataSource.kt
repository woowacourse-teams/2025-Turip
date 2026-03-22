package com.on.turip.data.turip.datasource

import com.on.turip.BuildConfig
import com.on.turip.core.network.ApiPath
import com.on.turip.core.result.TuripResult
import com.on.turip.data.result.safeApiCall
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
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultTuripSseStreamDataSource @Inject constructor(
    @SseHttpClient private val httpClient: HttpClient,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : TuripSseStreamDataSource {
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
                                    Timber.e(
                                        "튜립 SSE 이벤트 타입 누락. id=%s, dataLen=%s",
                                        event.id,
                                        event.data?.length,
                                    )
                                    return@collect
                                }
                            val data: String =
                                event.data ?: run {
                                    Timber.e(
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
                                    Timber.e(
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
