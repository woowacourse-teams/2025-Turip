package com.on.turip.core.domain.usecase

import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.turip.TuripType

class DeleteTuripUseCase(
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
