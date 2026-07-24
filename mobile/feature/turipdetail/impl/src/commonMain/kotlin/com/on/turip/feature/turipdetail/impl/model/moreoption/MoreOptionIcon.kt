package com.on.turip.feature.turipdetail.impl.model.moreoption

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource

@Immutable
sealed interface MoreOptionIcon {
    data class Vector(
        val imageVector: ImageVector,
    ) : MoreOptionIcon

    data class Resource(
        val resId: DrawableResource,
    ) : MoreOptionIcon
}
