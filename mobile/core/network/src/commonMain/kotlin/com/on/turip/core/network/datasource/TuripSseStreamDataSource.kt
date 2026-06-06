package com.on.turip.core.network.datasource

import com.on.turip.core.model.turip.TuripStreamEvent
import kotlinx.coroutines.flow.Flow

interface TuripSseStreamDataSource {
    fun streamTuripEvents(turipId: Long): Flow<TuripStreamEvent>
}
