package com.on.turip.feature.main.navigation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource

@Immutable
sealed interface NavigationIconModel {
    data class Vector(
        val imageVector: ImageVector,
    ) : NavigationIconModel

    data class PainterIcon(
        val drawRes: DrawableResource,
    ) : NavigationIconModel
}
