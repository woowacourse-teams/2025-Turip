package com.on.turip.feature.turipdetail.impl.model.moreoption

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class MoreOptionItem(
    val title: String,
    val icon: MoreOptionIcon,
    val color: Color,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)
