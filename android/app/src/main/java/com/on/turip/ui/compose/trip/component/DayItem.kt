package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.model.DayModel

@Composable
fun DayItem(
    dayModel: DayModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clickable(onClick = onClick)
                .width(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.trip_detail_trip_day, dayModel.day),
            style = TuripTheme.typography.title3,
            color = if (dayModel.isSelected) TuripTheme.colors.black else TuripTheme.colors.gray02,
            modifier = Modifier.padding(4.dp),
        )

        Box(
            modifier =
                Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(
                        color = if (dayModel.isSelected) TuripTheme.colors.primary else Color.Transparent,
                    ),
        )
    }
}

@Preview(showBackground = true, name = "선택")
@Composable
private fun SelectDayItemPreview() {
    TuripTheme {
        DayItem(
            dayModel = DayModel(1, true),
            onClick = { },
        )
    }
}

@Preview(showBackground = true, name = "미선택")
@Composable
private fun NotSelectDayItemPreview() {
    TuripTheme {
        DayItem(
            dayModel = DayModel(1, false),
            onClick = { },
        )
    }
}
