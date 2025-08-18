package com.on.turip.data.favorite.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.data.favorite.datasource.FavoriteRemoteDataSource
import com.on.turip.data.favorite.toDomain
import com.on.turip.data.favorite.toRequestDto
import com.on.turip.domain.favorite.PagedFavoriteContents
import com.on.turip.domain.favorite.repository.FavoriteRepository

class DefaultFavoriteRepository(
    private val favoriteRemoteDataSource: FavoriteRemoteDataSource,
) : FavoriteRepository {
    override suspend fun createFavorite(contentId: Long): TuripCustomResult<Unit> =
        favoriteRemoteDataSource.postFavorite(
            contentId.toRequestDto(),
        )

    override suspend fun deleteFavorite(contentId: Long): TuripCustomResult<Unit> = favoriteRemoteDataSource.deleteFavorite(contentId)

    override suspend fun loadFavoriteContents(
        size: Int,
        lastId: Long,
    ): TuripCustomResult<PagedFavoriteContents> =
        favoriteRemoteDataSource
            .getFavoriteContents(
                size,
                lastId,
            ).mapCatching { it.toDomain() }
}
