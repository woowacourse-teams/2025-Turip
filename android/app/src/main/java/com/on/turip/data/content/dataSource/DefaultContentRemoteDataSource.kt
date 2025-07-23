package com.on.turip.data.content.dataSource

import com.on.turip.data.content.dto.ContentInformationCountResponse
import com.on.turip.data.content.service.ContentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultContentRemoteDataSource(
    private val contentService: ContentService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : ContentRemoteDataSource {
    override suspend fun getContentsSize(region: String): Result<ContentInformationCountResponse> =
        withContext(coroutineContext) {
            runCatching { contentService.getContentsCount(region) }
        }
}
