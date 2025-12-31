package com.on.turip.data.favorite.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.data.result.mapCatching
import com.on.turip.data.favorite.datasource.FavoriteRemoteDataSource
import com.on.turip.data.favorite.toDomain
import com.on.turip.data.favorite.toRequestDto
import com.on.turip.domain.favorite.PagedFavoriteContents
import com.on.turip.domain.favorite.repository.FavoriteRepository
import javax.inject.Inject

class DefaultFavoriteRepository @Inject constructor(
    private val favoriteRemoteDataSource: FavoriteRemoteDataSource,
) : FavoriteRepository {
    override suspend fun createFavorite(contentId: Long): TuripResult<Unit> =
        favoriteRemoteDataSource.postFavorite(
            contentId.toRequestDto(),
        )

    override suspend fun deleteFavorite(contentId: Long): TuripResult<Unit> =
        favoriteRemoteDataSource.deleteFavorite(contentId)

    override suspend fun loadFavoriteContents(
        size: Int,
        lastId: Long,
    ): TuripResult<PagedFavoriteContents> =
        favoriteRemoteDataSource
            .getFavoriteContents(
                size,
                lastId,
            ).mapCatching { it.toDomain() }
}
