package com.on.turip.feature.trip.impl.turipselection.component

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.ic_turip_plus
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_add_turip_description
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_turip_selection_confirm
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_turip_selection_title
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.trip.impl.turipselection.TuripSelectionModel
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TuripsContent(
    placeName: String,
    enableConfirm: Boolean,
    turips: ImmutableList<TuripSelectionModel>,
    onAddTuripClick: () -> Unit,
    onNavigateToTurip: (turipId: Long) -> Unit,
    onTuripPlaceClick: (turip: TuripSelectionModel) -> Unit,
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
                text = stringResource(Res.string.trip_detail_bottom_sheet_turip_selection_title),
                style = TuripTheme.typography.body2,
                color = TuripTheme.colors.gray03,
                modifier = Modifier.padding(start = TuripTheme.spacing.small),
            )
        },
        actions = {
            IconButton(onClick = onAddTuripClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_turip_plus),
                    contentDescription = stringResource(Res.string.trip_detail_bottom_sheet_add_turip_description),
                )
            }
        },
    )
}

@Composable
private fun Turips(
    listState: LazyListState,
    turips: ImmutableList<TuripSelectionModel>,
    onNavigateToTurip: (turipId: Long) -> Unit,
    onTuripPlaceClick: (turip: TuripSelectionModel) -> Unit,
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
                onItemClick = { onNavigateToTurip(turip.id) },
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
            text = stringResource(Res.string.trip_detail_bottom_sheet_turip_selection_confirm),
            style = TuripTheme.typography.title2,
            color = contentColor,
        )
    }
}
