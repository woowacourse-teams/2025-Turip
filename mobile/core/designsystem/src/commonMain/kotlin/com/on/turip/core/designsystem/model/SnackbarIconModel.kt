package com.on.turip.core.designsystem.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
sealed interface SnackbarIconModel {
    data class Vector(
        val imageVector: ImageVector,
    ) : SnackbarIconModel

    data class PainterIcon(
        val painter: Painter,
    ) : SnackbarIconModel
}
