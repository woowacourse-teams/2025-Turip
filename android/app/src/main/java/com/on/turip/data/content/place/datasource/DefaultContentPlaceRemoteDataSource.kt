package com.on.turip.data.content.place.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.content.place.dto.ContentPlacesResponse
import com.on.turip.data.content.place.service.ContentPlaceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultContentPlaceRemoteDataSource @Inject constructor(
    private val contentPlaceService: ContentPlaceService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : ContentPlaceRemoteDataSource {
    override suspend fun getTrip(contentId: Long): TuripCustomResult<ContentPlacesResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentPlaceService.getTrip(contentId) }
        }
}
