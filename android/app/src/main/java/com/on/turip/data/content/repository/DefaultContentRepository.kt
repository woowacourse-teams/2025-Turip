package com.on.turip.data.content.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.content.datasource.ContentRemoteDataSource
import com.on.turip.data.content.toDomain
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.domain.content.repository.ContentRepository
import javax.inject.Inject

class DefaultContentRepository @Inject constructor(
    private val contentRemoteDataSource: ContentRemoteDataSource,
) : ContentRepository {
    override suspend fun loadContentsSizeByRegion(regionCategoryName: String): TuripResult<Int> =
        contentRemoteDataSource
            .getContentsSizeByRegion(regionCategoryName)
            .mapCatching { it.count }

    override suspend fun loadContentsSizeByKeyword(keyword: String): TuripResult<Int> =
        contentRemoteDataSource
            .getContentsSizeByKeyword(keyword)
            .mapCatching { it.count }

    override suspend fun loadContentsByRegion(
        regionCategoryName: String,
        size: Int,
        lastId: Long,
    ): TuripResult<PagedContentsResult> =
        contentRemoteDataSource
            .getContentsByRegion(regionCategoryName, size, lastId)
            .mapCatching { it.toDomain() }

    override suspend fun loadContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): TuripResult<PagedContentsResult> =
        contentRemoteDataSource
            .getContentsByKeyword(keyword, size, lastId)
            .mapCatching { it.toDomain() }

    override suspend fun loadContent(contentId: Long): TuripResult<Content> =
        contentRemoteDataSource
            .getContentDetail(contentId)
            .mapCatching { it.toDomain() }

    override suspend fun loadPopularFavoriteContents(size: Int): TuripResult<List<UsersLikeContent>> =
        contentRemoteDataSource
            .getUsersLikeContents(size)
            .mapCatching { it.toDomain() }
}
