package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

class DefaultTuripStreamService(
    private val httpClient: HttpClient,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : TuripStreamService {
    override fun streamTuripEvents(turipId: Long): Flow<ServerSentEvent> =
        flow {
            httpClient.sse(
                urlString = "${BuildKonfig.BASE_URL}${ApiPath.V1}turips/$turipId/stream",
            ) {
                incoming.collect { event: ServerSentEvent ->
                    emit(event)
                }
            }
        }.flowOn(coroutineContext)
}
