package com.on.turip.ui.folder.model

import androidx.annotation.StringRes
import com.on.turip.R
import com.on.turip.domain.folder.TuripNameStatus

enum class TuripNameStatusModel(
    @StringRes val errorMessage: Int?,
    val isConfirmEnabled: Boolean,
) {
    OK(null, true),
    EMPTY(null, false),
    DUPLICATE_NAME(R.string.all_turip_name_error_duplicate, false),
    DEFAULT_TURIP_NAME(R.string.all_turip_name_same_default_name_error, false),
    MAX_LENGTH_TURIP_NAME(R.string.all_turip_name_warning_max_length, true),
    OUT_OF_BOUND_LENGTH(R.string.all_turip_name_error_out_of_bound, false),
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
