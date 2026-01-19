package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.on.turip.ui.compose.trip.model.DayModel.Companion.ALL_PLACE
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun Days(
    days: ImmutableList<DayModel>,
    onDayClick: (day: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
    ) {
        items(items = days, key = { it.day }) { dayModel: DayModel ->
            DayItem(
                dayModel = dayModel,
                onClick = { onDayClick(dayModel.day) },
            )
        }
    }
}

@Composable
private fun DayItem(
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
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text =
                if (dayModel.day == ALL_PLACE) {
                    stringResource(R.string.trip_detail_trip_all_place)
                } else {
                    stringResource(R.string.trip_detail_trip_day, dayModel.day)
                },
            style = TuripTheme.typography.title3,
            color = if (dayModel.isSelected) TuripTheme.colors.black else TuripTheme.colors.gray02,
            modifier = Modifier.padding(TuripTheme.spacing.extraSmall),
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

@Preview(showBackground = true)
@Composable
private fun DaysPreview() {
    val days = persistentListOf(DayModel(1, false), DayModel(2, true), DayModel(3, false))
    TuripTheme {
        Days(
            days = days,
            onDayClick = { },
        )
    }
}
