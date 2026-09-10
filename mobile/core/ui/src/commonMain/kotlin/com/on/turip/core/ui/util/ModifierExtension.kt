package com.on.turip.core.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.noRippleClickable(
    onClickLabel: String? = null,
    onClick: () -> Unit,
): Modifier =
    composed {
        this.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClickLabel = onClickLabel,
        ) { onClick() }
    }
