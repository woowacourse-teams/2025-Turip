package com.on.turip.feature.turipdetail.impl.model

import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_turip_name_error_duplicate
import com.on.turip.core.designsystem.generated.resources.all_turip_name_error_out_of_bound
import com.on.turip.core.designsystem.generated.resources.all_turip_name_same_default_name_error
import com.on.turip.core.designsystem.generated.resources.all_turip_name_warning_max_length
import org.jetbrains.compose.resources.StringResource

enum class TuripNameStatusModel(
    val isConfirmEnabled: Boolean,
    val errorMessage: StringResource?,
) {
    OK(true, null),
    EMPTY(false, null),
    DUPLICATE_NAME(false, Res.string.all_turip_name_error_duplicate),
    DEFAULT_TURIP_NAME(false, Res.string.all_turip_name_same_default_name_error),
    MAX_LENGTH_TURIP_NAME(true, Res.string.all_turip_name_warning_max_length),
    OUT_OF_BOUND_LENGTH(false, Res.string.all_turip_name_error_out_of_bound),
}
