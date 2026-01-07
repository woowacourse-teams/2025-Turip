package com.on.turip.ui.compose.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

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
                    shape = RoundedCornerShape(24.dp),
                ).border(
                    width = 1.dp,
                    color = TuripTheme.colors.border,
                    shape = RoundedCornerShape(24.dp),
                ).padding(horizontal = 12.dp, vertical = 4.dp),
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
    RegionChip(
        regionName = "속초",
    )
}
