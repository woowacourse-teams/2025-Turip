package com.on.turip.feature.main.navigation.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
sealed interface NavigationIconModel {
    data class Vector(
        val imageVector: ImageVector,
    ) : NavigationIconModel

    data class PainterIcon(
        @DrawableRes val drawRes: Int,
    ) : NavigationIconModel
}
