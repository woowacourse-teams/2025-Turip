package com.on.turip.data.content.dataSource

import com.on.turip.data.content.dto.ContentDetailResponse
import com.on.turip.data.content.dto.ContentInformationCountResponse
import com.on.turip.data.content.dto.ContentsInformationResponse
import com.on.turip.data.content.service.ContentService
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultContentRemoteDataSource(
    private val contentService: ContentService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : ContentRemoteDataSource {
    override suspend fun getContentsSize(region: String): Result<ContentInformationCountResponse> =
        withContext(coroutineContext) {
            runCatching { contentService.getContentsCount(region) }
        }

    override suspend fun getContents(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<ContentsInformationResponse> =
        withContext(coroutineContext) {
            runCatching {
                contentService.getContentsInformation(
                    region = region,
                    size = size,
                    lastId = lastId,
                )
            }
        }

    override suspend fun getContentDetail(contentId: Long): Result<ContentDetailResponse> =
        withContext(coroutineContext) {
            runCatching { contentService.getContentDetail(contentId) }
        }
}
