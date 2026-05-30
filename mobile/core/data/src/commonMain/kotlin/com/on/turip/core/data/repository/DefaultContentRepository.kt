package com.on.turip.core.data.repository

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.data.mapper.toDomain
import com.on.turip.core.network.datasource.ContentDatasource

class DefaultContentRepository(
    private val contentDatasource: ContentDatasource,
) : ContentRepository {
    override suspend fun getContent(contentId: Long): Result<Content> =
        safeApiCall { contentDatasource.getContent(contentId).toDomain() }

    override suspend fun loadPopularFavoriteContents(size: Int): Result<List<UsersLikeContent>> =
        safeApiCall { contentDatasource.getPopularContents(size).map { it.toDomain() } }

    override suspend fun loadContentsByKeyword(keyword: String, size: Int, lastId: Long): Result<List<Content>> =
        safeApiCall { contentDatasource.getContentsByKeyword(keyword, size, lastId).map { it.toDomain() } }

    override suspend fun loadContentsSizeByKeyword(keyword: String): Result<Int> =
        safeApiCall { contentDatasource.getContentsSizeByKeyword(keyword) }

    override suspend fun loadContentsByRegion(regionCategory: String, size: Int, lastId: Long): Result<List<Content>> =
        safeApiCall { contentDatasource.getContentsByRegion(regionCategory, size, lastId).map { it.toDomain() } }

    override suspend fun loadContentsSizeByRegion(regionCategory: String): Result<Int> =
        safeApiCall { contentDatasource.getContentsSizeByRegion(regionCategory) }
}