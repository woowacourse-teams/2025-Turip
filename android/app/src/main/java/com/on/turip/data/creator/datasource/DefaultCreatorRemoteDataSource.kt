package com.on.turip.data.creator.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.creator.dto.CreatorResponse
import com.on.turip.data.creator.service.CreatorService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultCreatorRemoteDataSource(
    private val creatorService: CreatorService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : CreatorRemoteDataSource {
    override suspend fun getCreator(creatorId: Long): TuripCustomResult<CreatorResponse> =
        withContext(coroutineContext) {
            safeApiCall { creatorService.getCreator(creatorId) }
        }
}
