package com.on.turip.domain.favorite.usecase

import com.on.turip.domain.favorite.repository.FavoriteRepository

class UpdateFavoriteUseCase(
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke(
        isFavorite: Boolean,
        contentId: Long,
    ): Result<Unit> =
        if (isFavorite) {
            favoriteRepository.createFavorite(contentId)
        } else {
            favoriteRepository.deleteFavorite(contentId)
        }
}
