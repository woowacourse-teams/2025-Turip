package com.on.turip.domain.favorite.usecase

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.favorite.repository.FavoriteRepository
import javax.inject.Inject

class UpdateFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke(
        isFavorite: Boolean,
        contentId: Long,
    ): TuripCustomResult<Unit> =
        if (isFavorite) {
            favoriteRepository.createFavorite(contentId)
        } else {
            favoriteRepository.deleteFavorite(contentId)
        }
}
