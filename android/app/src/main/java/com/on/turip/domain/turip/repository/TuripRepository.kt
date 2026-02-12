package com.on.turip.domain.turip.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.turip.Turip

interface TuripRepository {
    suspend fun loadTurips(): TuripResult<List<Turip>>

    suspend fun createTurip(name: String): TuripResult<Turip>

    suspend fun updateTurip(
        turipId: Long,
        updateName: String,
    ): TuripResult<Unit>

    suspend fun deleteTurip(turipId: Long): TuripResult<Unit>

    suspend fun loadTuripsByPlaceId(placeId: Long): TuripResult<List<Turip>>

    suspend fun loadTuripPlaces(turipId: Long): TuripResult<List<TuripPlace>>

    suspend fun createTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun deleteTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun updateTuripPlacesOrder(
        turipId: Long,
        updatedOrder: List<Long>,
    ): TuripResult<Unit>

    suspend fun updatePlaceTurips(
        placeId: Long,
        turipIds: List<Long>,
    ): TuripResult<Unit>
}
