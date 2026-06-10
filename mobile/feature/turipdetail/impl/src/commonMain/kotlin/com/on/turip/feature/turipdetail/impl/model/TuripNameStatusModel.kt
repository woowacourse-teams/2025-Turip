package com.on.turip.feature.turipdetail.impl.model

enum class TuripNameStatusModel(
    val isConfirmEnabled: Boolean,
) {
    OK(true),
    EMPTY(false),
    DUPLICATE_NAME(false),
    DEFAULT_TURIP_NAME(false),
    MAX_LENGTH_TURIP_NAME(true),
    OUT_OF_BOUND_LENGTH(false),
}
