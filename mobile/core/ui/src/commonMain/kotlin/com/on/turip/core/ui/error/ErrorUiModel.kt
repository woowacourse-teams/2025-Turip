package com.on.turip.core.ui.error

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

@Immutable
data class ErrorUiModel(
    val imageRes: DrawableResource,
    val titleRes: StringResource,
    val descriptionRes: StringResource,
    val retryTextRes: StringResource,
)
