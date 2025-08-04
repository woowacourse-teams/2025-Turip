package com.on.turip.data.favorite.repository

import com.on.turip.data.favorite.dataSource.FavoriteRemoteDataSource
import com.on.turip.data.favorite.toFavoriteAddRequest
import com.on.turip.domain.favorite.repository.FavoriteRepository

class DefaultFavoriteRepository(
    private val favoriteRemoteDataSource: FavoriteRemoteDataSource,
) : FavoriteRepository {
    override suspend fun updateFavorite(
        isFavorite: Boolean,
        fid: String,
        contentId: Long,
    ) {
        if (isFavorite) {
            favoriteRemoteDataSource.postFavorite(fid, contentId.toFavoriteAddRequest())
        } else {
        }
    }
}
