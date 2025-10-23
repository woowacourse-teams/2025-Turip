package com.on.turip.data.content.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.content.dto.ContentDetailResponse
import com.on.turip.data.content.dto.ContentInformationCountResponse
import com.on.turip.data.content.dto.ContentsInformationResponse
import com.on.turip.data.content.dto.UsersLikeContentsResponse
import com.on.turip.data.content.service.ContentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultContentRemoteDataSource @Inject constructor(
    private val contentService: ContentService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : ContentRemoteDataSource {
    override suspend fun getContentsSizeByRegion(regionCategoryName: String): TuripCustomResult<ContentInformationCountResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentService.getContentsCountByRegion(regionCategoryName) }
        }

    override suspend fun getContentsSizeByKeyword(keyword: String): TuripCustomResult<ContentInformationCountResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentService.getContentsCountByKeyword(keyword) }
        }

    override suspend fun getContentsByRegion(
        regionCategoryName: String,
        size: Int,
        lastId: Long,
    ): TuripCustomResult<ContentsInformationResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                contentService.getContentsByRegion(
                    regionCategoryName = regionCategoryName,
                    size = size,
                    lastId = lastId,
                )
            }
        }

    override suspend fun getContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): TuripCustomResult<ContentsInformationResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                contentService.getContentsByKeyword(
                    keyword = keyword,
                    size = size,
                    lastId = lastId,
                )
            }
        }

    override suspend fun getContentDetail(contentId: Long): TuripCustomResult<ContentDetailResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentService.getContentDetail(contentId) }
        }

    override suspend fun getUsersLikeContents(size: Int): TuripCustomResult<UsersLikeContentsResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentService.getUsersLikeContents(size) }
        }
}
