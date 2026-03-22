package com.on.turip.data.turip.service

import com.on.turip.core.result.TuripResult
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.flow.Flow

interface TuripStreamService {
    fun streamTuripEvents(turipId: Long): Flow<TuripResult<ServerSentEvent>>
}
