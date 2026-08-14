package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun RegionChip(
    regionName: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .wrapContentSize()
                .background(
                    color = TuripTheme.colors.chipBackground,
                    shape = TuripTheme.shape.chip,
                ).border(
                    width = 1.dp,
                    color = TuripTheme.colors.border,
                    shape = TuripTheme.shape.chip,
                ).padding(horizontal = TuripTheme.spacing.medium, vertical = TuripTheme.spacing.extraSmall),
    ) {
        Text(
            text = regionName,
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.black,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegionChipPreview() {
    TuripTheme {
        RegionChip(
            regionName = "속초",
        )
    }
}
