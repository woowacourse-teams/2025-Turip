package com.on.turip.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme

private val DEFAULT_LOADING_INDICATOR_SIZE = 60.dp

@Composable
fun TuripLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = DEFAULT_LOADING_INDICATOR_SIZE,
    color: Color = TuripTheme.colors.primary,
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
    )
}
