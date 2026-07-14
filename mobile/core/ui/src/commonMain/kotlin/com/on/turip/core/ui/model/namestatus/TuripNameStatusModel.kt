package com.on.turip.core.ui.model.namestatus

import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_turip_name_error_duplicate
import com.on.turip.core.designsystem.generated.resources.all_turip_name_error_out_of_bound
import com.on.turip.core.designsystem.generated.resources.all_turip_name_same_default_name_error
import com.on.turip.core.designsystem.generated.resources.all_turip_name_warning_max_length
import com.on.turip.core.model.turip.TuripNameStatus
import com.on.turip.core.ui.model.turip.TuripEditModel
import org.jetbrains.compose.resources.StringResource

enum class TuripNameStatusModel(
    val errorMessage: StringResource?,
    val isConfirmEnabled: Boolean,
) {
    OK(null, true),
    EMPTY(null, false),
    DUPLICATE_NAME(Res.string.all_turip_name_error_duplicate, false),
    DEFAULT_TURIP_NAME(Res.string.all_turip_name_same_default_name_error, false),
    MAX_LENGTH_TURIP_NAME(Res.string.all_turip_name_warning_max_length, true),
    OUT_OF_BOUND_LENGTH(Res.string.all_turip_name_error_out_of_bound, false),
    ;

    companion object {
        fun of(
            turipName: String,
            originTurips: List<TuripEditModel>,
        ): TuripNameStatusModel {
            val originTuripNames: LinkedHashSet<String> =
                originTurips.map { it.name }.toCollection(LinkedHashSet())

            val turipNameStatus: TuripNameStatus =
                TuripNameStatus.of(turipName, originTuripNames)
            return when (turipNameStatus) {
                TuripNameStatus.EMPTY -> EMPTY
                TuripNameStatus.MAX_LENGTH_TURIP_NAME -> MAX_LENGTH_TURIP_NAME
                TuripNameStatus.OUT_OF_BOUND_LENGTH -> OUT_OF_BOUND_LENGTH
                TuripNameStatus.DEFAULT_TURIP_NAME -> DEFAULT_TURIP_NAME
                TuripNameStatus.DUPLICATE_NAME -> DUPLICATE_NAME
                TuripNameStatus.OK -> OK
            }
        }
    }
}
