package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.data.datasource.ContentRemoteDataSource
import com.on.turip.core.data.dto.content.ContentDetailResponse
import com.on.turip.core.data.dto.content.ContentInformationCountResponse
import com.on.turip.core.data.dto.content.ContentPlacesResponse
import com.on.turip.core.data.dto.content.ContentsInformationResponse
import com.on.turip.core.data.dto.content.UsersLikeContentsResponse
import com.on.turip.core.network.service.ContentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultContentRemoteDataSource(
    private val contentService: ContentService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : ContentRemoteDataSource {
    override suspend fun getContentsSizeByRegion(regionCategoryName: String): TuripResult<ContentInformationCountResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentService.getContentsCountByRegion(regionCategoryName) }
        }

    override suspend fun getContentsSizeByKeyword(keyword: String): TuripResult<ContentInformationCountResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentService.getContentsCountByKeyword(keyword) }
        }

    override suspend fun getContentsByRegion(
        regionCategoryName: String,
        size: Int,
        lastId: Long,
    ): TuripResult<ContentsInformationResponse> =
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
    ): TuripResult<ContentsInformationResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                contentService.getContentsByKeyword(
                    keyword = keyword,
                    size = size,
                    lastId = lastId,
                )
            }
        }

    override suspend fun getContentDetail(contentId: Long): TuripResult<ContentDetailResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentService.getContentDetail(contentId) }
        }

    override suspend fun getUsersLikeContents(size: Int): TuripResult<UsersLikeContentsResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentService.getUsersLikeContents(size) }
        }

    override suspend fun getTrip(contentId: Long): TuripResult<ContentPlacesResponse> =
        withContext(coroutineContext) {
            safeApiCall { contentService.getTrip(contentId) }
        }
}
