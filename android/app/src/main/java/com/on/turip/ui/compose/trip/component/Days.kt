package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.common.collect.ImmutableList
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.model.DayModel

@Composable
fun Days(
    state: LazyListState,
    days: ImmutableList<DayModel>,
    onDayClick: (day: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        state = state,
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(items = days, key = { it.day }) { dayModel: DayModel ->
            DayItem(
                dayModel = dayModel,
                onClick = { onDayClick(dayModel.day) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DaysPreview() {
    val listState = rememberLazyListState()
    val days = ImmutableList.of(DayModel(1, false), DayModel(2, true), DayModel(3, false))
    TuripTheme {
        Days(
            state = listState,
            days = days,
            onDayClick = { },
        )
    }
}
