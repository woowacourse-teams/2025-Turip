package com.on.turip.ui.compose.favorite.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.on.turip.ui.compose.favorite.component.MoreOptionIcon

@Immutable
data class MoreOptionItem(
    val title: String,
    val icon: MoreOptionIcon,
    val color: Color,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)
