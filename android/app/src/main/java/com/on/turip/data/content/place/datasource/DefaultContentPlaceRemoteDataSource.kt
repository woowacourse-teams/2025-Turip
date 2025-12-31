package com.on.turip.data.content.place.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.result.safeApiCall
import com.on.turip.data.content.place.dto.ContentPlacesResponse
import com.on.turip.data.content.place.service.ContentPlaceService
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultContentPlaceRemoteDataSource @Inject constructor(
    private val contentPlaceService: ContentPlaceService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : ContentPlaceRemoteDataSource {
    override suspend fun getTrip(contentId: Long): TuripResult<ContentPlacesResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentPlaceService.getTrip(contentId) }
        }
}
