package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun VerticalDivider(
    height: Dp,
    thickness: Dp = 1.dp,
    color: Color = TuripTheme.colors.gray03,
) {
    Box(
        modifier =
            Modifier
                .height(height)
                .width(thickness)
                .background(color),
    )
}
