package com.on.turip.core.network.service

import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.flow.Flow

interface TuripStreamService {
    fun streamTuripEvents(turipId: Long): Flow<ServerSentEvent>
}
