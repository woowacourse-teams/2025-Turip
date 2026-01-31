package com.on.turip.data.content.place.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.content.place.dto.ContentPlacesResponse
import com.on.turip.data.content.service.ContentService
import com.on.turip.data.result.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultContentPlaceRemoteDataSource @Inject constructor(
    private val contentService: ContentService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : ContentPlaceRemoteDataSource {
    override suspend fun getTrip(contentId: Long): TuripResult<ContentPlacesResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentService.getTrip(contentId) }
        }
}
