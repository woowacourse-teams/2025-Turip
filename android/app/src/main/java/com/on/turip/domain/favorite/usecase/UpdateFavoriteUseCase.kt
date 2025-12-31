package com.on.turip.domain.favorite.usecase

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.favorite.repository.FavoriteRepository
import javax.inject.Inject

class UpdateFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke(
        isFavorite: Boolean,
        contentId: Long,
    ): TuripResult<Unit> =
        if (isFavorite) {
            favoriteRepository.createFavorite(contentId)
        } else {
            favoriteRepository.deleteFavorite(contentId)
        }
}
