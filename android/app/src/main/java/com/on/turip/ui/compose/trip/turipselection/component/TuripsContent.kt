package com.on.turip.ui.compose.trip.turipselection.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.main.favorite.model.TuripModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun TuripsContent(
    placeName: String,
    enableConfirm: Boolean,
    turips: ImmutableList<TuripModel>,
    onAddTuripClick: () -> Unit,
    onNavigateToTurip: (turipId: Long, turipName: String) -> Unit,
    onTuripPlaceClick: (turip: TuripModel) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    Column(modifier = modifier.fillMaxWidth()) {
        Header(placeName, onAddTuripClick)

        Turips(
            listState = listState,
            turips = turips,
            onNavigateToTurip = onNavigateToTurip,
            onTuripPlaceClick = onTuripPlaceClick,
            modifier = Modifier.weight(1f),
        )

        ConfirmButton(
            enabled = enableConfirm,
            onClick = onConfirmClick,
            modifier =
                Modifier.padding(
                    horizontal = TuripTheme.spacing.extraLarge,
                    vertical = TuripTheme.spacing.small,
                ),
        )
    }
}

@Composable
private fun Header(
    placeName: String,
    onAddTuripClick: () -> Unit,
) {
    PlaceTuripSelectionBottomSheetHeader(
        title = placeName,
        navigation = {
            Text(
                text = stringResource(R.string.trip_detail_bottom_sheet_turip_selection_title),
                style = TuripTheme.typography.body2,
                color = TuripTheme.colors.gray03,
                modifier = Modifier.padding(start = TuripTheme.spacing.small),
            )
        },
        actions = {
            IconButton(
                onClick = onAddTuripClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_turip_plus),
                    contentDescription = stringResource(R.string.trip_detail_bottom_sheet_add_turip_description),
                )
            }
        },
    )
}

@Composable
private fun Turips(
    listState: LazyListState,
    turips: ImmutableList<TuripModel>,
    onNavigateToTurip: (turipId: Long, turipName: String) -> Unit,
    onTuripPlaceClick: (turip: TuripModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = TuripTheme.spacing.small),
    ) {
        items(items = turips, key = { it.id }) { turip ->
            TuripItem(
                turip = turip,
                onItemClick = { onNavigateToTurip(turip.id, turip.name) },
                onTuripPlaceClick = { onTuripPlaceClick(turip) },
            )
        }
    }
}

@Composable
private fun ConfirmButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (enabled) TuripTheme.colors.primary else TuripTheme.colors.gray02
    val contentColor = if (enabled) TuripTheme.colors.white else TuripTheme.colors.gray03

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = backgroundColor, shape = TuripTheme.shape.wideButton)
                .clip(shape = TuripTheme.shape.wideButton)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(vertical = TuripTheme.spacing.large),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.trip_detail_bottom_sheet_turip_selection_confirm),
            style = TuripTheme.typography.title2,
            color = contentColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TuripsContentPreview() {
    TuripTheme {
        Surface {
            TuripsContent(
                placeName = "부산 경포대 해수욕장 해수욕장 해수욕장 해수욕장",
                enableConfirm = false,
                turips =
                    persistentListOf(
                        TuripModel(1L, "서울 여행", 2, false),
                        TuripModel(3L, "캐나다 여행 여행 여행 여행 여행 여행 여행 여행 여행 여행", 3, true),
                        TuripModel(2L, "일본 여행", 1, true),
                    ),
                onAddTuripClick = { },
                onTuripPlaceClick = { },
                onNavigateToTurip = { _, _ -> },
                onConfirmClick = {},
            )
        }
    }
}
