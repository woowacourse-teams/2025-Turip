package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.content.ContentResponse
import com.on.turip.core.network.dto.content.UsersLikeContentResponse
import com.on.turip.core.network.service.ContentService

class DefaultContentDatasource(
    private val contentService: ContentService,
) : ContentDatasource {
    override suspend fun getContent(contentId: Long): ContentResponse =
        contentService.getContent(contentId)

    override suspend fun getPopularContents(size: Int): List<UsersLikeContentResponse> =
        contentService.getPopularContents(size)

    override suspend fun getContentsByKeyword(keyword: String, size: Int, lastId: Long): List<ContentResponse> =
        contentService.getContentsByKeyword(keyword, size, lastId)

    override suspend fun getContentsSizeByKeyword(keyword: String): Int =
        contentService.getContentsSizeByKeyword(keyword)

    override suspend fun getContentsByRegion(regionCategory: String, size: Int, lastId: Long): List<ContentResponse> =
        contentService.getContentsByRegion(regionCategory, size, lastId)

    override suspend fun getContentsSizeByRegion(regionCategory: String): Int =
        contentService.getContentsSizeByRegion(regionCategory)
}
