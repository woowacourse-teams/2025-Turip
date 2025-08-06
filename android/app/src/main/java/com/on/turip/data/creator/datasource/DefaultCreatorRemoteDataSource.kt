package com.on.turip.data.creator.datasource

import com.on.turip.data.creator.dto.CreatorResponse
import com.on.turip.data.creator.service.CreatorService
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultCreatorRemoteDataSource(
    private val creatorService: CreatorService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : CreatorRemoteDataSource {
    override suspend fun getCreator(creatorId: Long): Result<CreatorResponse> =
        withContext(coroutineContext) {
            runCatching { creatorService.getCreator(creatorId) }
        }
}
