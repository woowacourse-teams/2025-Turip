package com.on.turip.core.domain.turip

import com.on.turip.core.model.TuripStreamEvent
import kotlinx.coroutines.flow.Flow

interface TuripStreamService {
    fun observeTuripStream(turipId: Long): Flow<TuripStreamEvent>
}
