package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun Day(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(TuripTheme.spacing.small),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.trip_detail_trip_all_place),
                style = TuripTheme.typography.title3,
            )
        }

        Box(
            modifier =
                Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(color = TuripTheme.colors.primary),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DayPreview() {
    TuripTheme {
        Day(modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge))
    }
}
