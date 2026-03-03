package com.on.turip.ui.compose.turip.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class TuripChipModel(
    val text: String,
    val backgroundColor: Color,
    val textColor: Color,
)
