package com.on.turip.data.content.repository

import com.on.turip.data.content.dataSource.ContentRemoteDataSource
import com.on.turip.data.content.toDomain
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoData

class DefaultContentRepository(
    private val contentRemoteDataSource: ContentRemoteDataSource,
) : ContentRepository {
    override suspend fun loadContentsSizeByRegion(region: String): Result<Int> =
        contentRemoteDataSource
            .getContentsSizeByRegion(region)
            .mapCatching { it.count }

    override suspend fun loadContentsSizeByKeyword(keyword: String): Result<Int> =
        contentRemoteDataSource
            .getContentsSizeByKeyword(keyword)
            .mapCatching { it.count }

    override suspend fun loadContentsByRegion(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult> =
        contentRemoteDataSource
            .getContentsByRegion(region, size, lastId)
            .mapCatching { it.toDomain() }

    override suspend fun loadContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult> =
        contentRemoteDataSource
            .getContentsByKeyword(keyword, size, lastId)
            .mapCatching { it.toDomain() }

    override suspend fun loadContent(contentId: Long): Result<VideoData> =
        contentRemoteDataSource
            .getContentDetail(contentId)
            .mapCatching { it.toDomain() }
}
