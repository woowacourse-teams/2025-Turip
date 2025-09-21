package com.on.turip.ui.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun TuripTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = TuripTypography,
        content = content,
    )
}
