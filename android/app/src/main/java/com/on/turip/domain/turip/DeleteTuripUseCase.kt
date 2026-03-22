package com.on.turip.domain.turip

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.turip.repository.TuripRepository
import javax.inject.Inject

class DeleteTuripUseCase @Inject constructor(
    private val turipRepository: TuripRepository,
) {
    suspend operator fun invoke(
        turipId: Long,
        type: TuripType,
    ): TuripResult<Unit> =
        when (type) {
            TuripType.SOLO -> turipRepository.deleteTurip(turipId)
            TuripType.TOGETHER -> turipRepository.exitTurip(turipId)
        }
}
