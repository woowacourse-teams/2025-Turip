package com.on.turip.domain.favorite.usecase

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import timber.log.Timber
import javax.inject.Inject

class UpdateFavoritePlaceUseCase @Inject constructor(
    private val favoritePlaceRepository: FavoritePlaceRepository,
) {
    suspend operator fun invoke(
        favoriteFolderId: Long,
        placeId: Long,
        isFavorite: Boolean,
    ): TuripResult<Unit> =
        if (isFavorite) {
            favoritePlaceRepository
                .createFavoritePlace(favoriteFolderId, placeId)
                .onSuccess {
                    Timber.d("장소 찜에 넣기 성공")
                }.onFailure {
                    Timber.e("장소 찜에 넣기 실패")
                }
        } else {
            favoritePlaceRepository
                .deleteFavoritePlace(favoriteFolderId, placeId)
                .onSuccess {
                    Timber.d("장소 찜에 빼기 성공")
                }.onFailure {
                    Timber.e("장소 찜에 빼기 실패")
                }
        }
}
