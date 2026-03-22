package com.on.turip.data.turip.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.turip.TuripStreamEvent
import kotlinx.coroutines.flow.Flow

interface TuripSseStreamDataSource {
    fun streamTuripEvents(turipId: Long): Flow<TuripResult<TuripStreamEvent>>
}
