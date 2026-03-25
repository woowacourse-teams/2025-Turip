package com.on.turip.data.turip.service

import com.on.turip.BuildConfig
import com.on.turip.core.network.ApiPath
import com.on.turip.di.SseHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultTuripStreamService @Inject constructor(
    @SseHttpClient private val httpClient: HttpClient,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : TuripStreamService {
    override fun streamTuripEvents(turipId: Long): Flow<ServerSentEvent> =
        flow {
            httpClient.sse(
                urlString = "${BuildConfig.BASE_URL}${ApiPath.V1}turips/$turipId/stream",
            ) {
                incoming.collect { event: ServerSentEvent ->
                    emit(event)
                }
            }
        }.flowOn(coroutineContext)
}
