package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun ContentInfoChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .border(
                    width = 1.dp,
                    color = TuripTheme.colors.gray02,
                    shape = TuripTheme.shape.chip,
                ).padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            style = TuripTheme.typography.body2,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentInfoChipPreview() {
    TuripTheme {
        ContentInfoChip(
            text = "속초",
        )
    }
}
