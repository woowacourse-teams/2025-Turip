package com.on.turip.feature.home.impl.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
internal fun HomeAppBar(modifier: Modifier = Modifier) {
    TuripAppBar(
        start = {
            Text(
                text = "Turip",
                style = TuripTheme.typography.title1,
                color = TuripTheme.colors.primary,
            )
        },
        modifier = modifier,
    )
}
