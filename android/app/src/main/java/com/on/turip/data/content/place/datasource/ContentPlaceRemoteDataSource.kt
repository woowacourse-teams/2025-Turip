package com.on.turip.data.content.place.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.content.place.dto.ContentPlacesResponse

interface ContentPlaceRemoteDataSource {
    suspend fun getTrip(contentId: Long): TuripResult<ContentPlacesResponse>
}
