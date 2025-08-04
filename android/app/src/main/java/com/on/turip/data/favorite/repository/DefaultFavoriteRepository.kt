package com.on.turip.data.favorite.repository

import com.on.turip.data.favorite.dataSource.FavoriteRemoteDataSource
import com.on.turip.data.favorite.toFavoriteAddRequest
import com.on.turip.domain.favorite.repository.FavoriteRepository
import com.on.turip.domain.userStorage.TuripDeviceIdentifier

class DefaultFavoriteRepository(
    private val favoriteRemoteDataSource: FavoriteRemoteDataSource,
) : FavoriteRepository {
    override suspend fun updateFavorite(
        isFavorite: Boolean,
        turipDeviceIdentifier: TuripDeviceIdentifier,
        contentId: Long,
    ) {
        if (isFavorite) {
            favoriteRemoteDataSource.postFavorite(
                turipDeviceIdentifier.fid,
                contentId.toFavoriteAddRequest(),
            )
        } else {
            favoriteRemoteDataSource.deleteFavorite(
                turipDeviceIdentifier.fid,
                contentId,
            )
        }
    }
}
