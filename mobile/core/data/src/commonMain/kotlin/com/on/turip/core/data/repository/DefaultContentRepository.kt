package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.ContentRemoteDataSource
import com.on.turip.core.data.mapper.toDomain
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.model.content.Content
import com.on.turip.core.model.content.PagedContentsResult
import com.on.turip.core.model.content.UsersLikeContent
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.mapCatching
import com.on.turip.core.model.trip.Trip

class DefaultContentRepository(
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

    override suspend fun loadTripInfo(contentId: Long): TuripResult<Trip> =
        contentRemoteDataSource
            .getTrip(contentId)
            .mapCatching { it.toDomain() }
}
