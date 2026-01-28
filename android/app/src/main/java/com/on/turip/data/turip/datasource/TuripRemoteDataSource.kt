package com.on.turip.data.turip.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.turip.dto.TuripCreationResponse
import com.on.turip.data.turip.dto.TuripPatchRequest
import com.on.turip.data.turip.dto.TuripPostRequest
import com.on.turip.data.turip.dto.TuripsByPlaceResponse
import com.on.turip.data.turip.dto.TuripsResponse

interface TuripRemoteDataSource {
    suspend fun getTurips(): TuripResult<TuripsResponse>

    suspend fun postTurip(turipPostRequest: TuripPostRequest): TuripResult<TuripCreationResponse>

    suspend fun patchTurip(
        turipId: Long,
        turipPatchRequest: TuripPatchRequest,
    ): TuripResult<Unit>

    suspend fun deleteTurip(turipId: Long): TuripResult<Unit>

    suspend fun getTuripsByPlaceId(placeId: Long): TuripResult<TuripsByPlaceResponse>
}
