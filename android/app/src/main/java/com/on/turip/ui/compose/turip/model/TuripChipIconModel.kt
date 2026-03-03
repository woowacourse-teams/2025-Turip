package com.on.turip.ui.compose.turip.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
sealed interface TuripChipIconModel {
    data class Vector(
        val imageVector: ImageVector,
    ) : TuripChipIconModel

    data class PainterChipIcon(
        val painter: Painter,
    ) : TuripChipIconModel
}
