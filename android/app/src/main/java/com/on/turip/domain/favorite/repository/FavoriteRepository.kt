package com.on.turip.domain.favorite.repository

import com.on.turip.domain.userStorage.TuripDeviceIdentifier

interface FavoriteRepository {
    suspend fun updateFavorite(
        isFavorite: Boolean,
        turipDeviceIdentifier: TuripDeviceIdentifier,
        contentId: Long,
    ): Result<Unit>
}
