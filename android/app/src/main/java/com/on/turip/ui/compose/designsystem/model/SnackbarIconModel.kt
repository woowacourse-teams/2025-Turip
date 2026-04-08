package com.on.turip.ui.compose.designsystem.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
sealed interface SnackbarIconModel {
    data class Vector(
        val imageVector: ImageVector,
    ) : SnackbarIconModel

    data class Painter(
        @param:DrawableRes val drawableRes: Int,
    ) : SnackbarIconModel
}
