package com.on.turip.domain.folder.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.folder.Turip

interface TuripRepository {
    suspend fun loadTurips(): TuripResult<List<Turip>>

    suspend fun createTurip(name: String): TuripResult<Turip>

    suspend fun updateTurip(
        turipId: Long,
        updateName: String,
    ): TuripResult<Unit>

    suspend fun deleteTurip(turipId: Long): TuripResult<Unit>

    suspend fun loadTuripsByPlaceId(placeId: Long): TuripResult<List<Turip>>
}
