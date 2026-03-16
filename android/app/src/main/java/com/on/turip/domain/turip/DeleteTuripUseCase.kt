package com.on.turip.domain.turip

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.compose.turip.model.TuripTypeModel
import javax.inject.Inject

class DeleteTuripUseCase @Inject constructor(
    private val turipRepository: TuripRepository,
) {
    suspend operator fun invoke(
        turipId: Long,
        type: TuripTypeModel,
    ): TuripResult<Unit> =
        when (type) {
            TuripTypeModel.SOLO -> turipRepository.deleteTurip(turipId)
            TuripTypeModel.TOGETHER -> turipRepository.exitTurip(turipId)
        }
}
