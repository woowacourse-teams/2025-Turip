package com.on.turip.data.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

/**
 * presentation/ui layer
 */
@Immutable
data class ErrorUiModel(
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val retryTextRes: Int,
)
