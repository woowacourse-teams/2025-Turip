package com.on.turip.domain.favorite.usecase

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.favorite.repository.TuripPlaceRepository
import javax.inject.Inject
import timber.log.Timber

class UpdateTuripPlaceUseCase @Inject constructor(
    private val turipPlaceRepository: TuripPlaceRepository,
) {
    suspend operator fun invoke(
        turipId: Long,
        placeId: Long,
        isTuripPlace: Boolean,
    ): TuripResult<Unit> =
        if (isTuripPlace) {
            turipPlaceRepository
                .createTuripPlace(turipId, placeId)
                .onSuccess {
                    Timber.d("튜립에 장소 추가 성공")
                }.onFailure {
                    Timber.e("튜립에 장소 추가 실패")
                }
        } else {
            turipPlaceRepository
                .deleteTuripPlace(turipId, placeId)
                .onSuccess {
                    Timber.d("튜립에서 장소 제거 성공")
                }.onFailure {
                    Timber.e("튜립에서 장소 제거 실패")
                }
        }
}
