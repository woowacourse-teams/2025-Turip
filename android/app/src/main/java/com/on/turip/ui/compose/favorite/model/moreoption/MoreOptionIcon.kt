package com.on.turip.ui.compose.favorite.model.moreoption

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
sealed interface MoreOptionIcon {
    data class Vector(
        val imageVector: ImageVector,
    ) : MoreOptionIcon

    data class Resource(
        val resId: Int,
    ) : MoreOptionIcon
}
