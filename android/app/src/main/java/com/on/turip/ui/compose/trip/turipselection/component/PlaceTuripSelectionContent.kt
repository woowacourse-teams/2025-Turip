package com.on.turip.ui.compose.trip.turipselection.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.trip.turipselection.PlaceTuripSelectionScreenMode
import com.on.turip.ui.compose.trip.turipselection.PlaceTuripSelectionUiState
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.model.TuripModel
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PlaceTuripSelectionContent(
    uiState: PlaceTuripSelectionUiState,
    onBackFromTuripDetail: () -> Unit,
    onDismissRequest: () -> Unit,
    onAddTuripClick: () -> Unit,
    onTuripPlaceClickAtTurips: (turipModel: TuripModel) -> Unit,
    onNavigateToTurip: (turipId: Long, turipName: String) -> Unit,
    onConfirmClick: () -> Unit,
    onMapClick: (mapModel: MapModel) -> Unit,
    onTuripPlaceClickAtTuripDetail: (place: TuripPlaceModel) -> Unit,
    onShareClick: () -> Unit,
    onDragStart: () -> Unit,
    onDragPlace: (from: Int, to: Int) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {
        when (uiState.screenMode) {
            is PlaceTuripSelectionScreenMode.TuripDetail -> onBackFromTuripDetail()
            is PlaceTuripSelectionScreenMode.Turips -> onDismissRequest()
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .padding(top = TuripTheme.spacing.medium)
                    .navigationBarsPadding(),
        ) {
            AnimatedVisibility(
                visible = uiState.screenMode is PlaceTuripSelectionScreenMode.Turips,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                CloseButton(
                    onCloseClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            when (val mode = uiState.screenMode) {
                PlaceTuripSelectionScreenMode.Turips -> {
                    TuripsContent(
                        placeName = uiState.placeName,
                        enableConfirm = uiState.isChanged,
                        turips = uiState.turips,
                        onAddTuripClick = onAddTuripClick,
                        onTuripPlaceClick = onTuripPlaceClickAtTurips,
                        onNavigateToTurip = onNavigateToTurip,
                        onConfirmClick = onConfirmClick,
                    )
                }

                is PlaceTuripSelectionScreenMode.TuripDetail -> {
                    TuripDetail(
                        turipName = mode.turipName,
                        places = uiState.selectedTuripPlaces,
                        onMapClick = onMapClick,
                        onTuripPlaceClick = onTuripPlaceClickAtTuripDetail,
                        onBackClick = onBackFromTuripDetail,
                        onShareClick = onShareClick,
                        onDragStart = onDragStart,
                        onDragPlace = onDragPlace,
                        onDragEnd = onDragEnd,
                    )
                }
            }
        }
    }
}

@Composable
private fun CloseButton(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(end = TuripTheme.spacing.medium)) {
        IconButton(
            onClick = onCloseClick,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .size(32.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.all_close_description),
                modifier = Modifier.size(16.dp),
                tint = TuripTheme.colors.gray04,
            )
        }
    }
}

@Preview(name = "튜립 목록", showBackground = true)
@Composable
private fun PlaceTuripSelectionContentTuripsPreview() {
    TuripTheme {
        Surface(color = TuripTheme.colors.white) {
            PlaceTuripSelectionContent(
                uiState =
                    PlaceTuripSelectionUiState(
                        screenMode = PlaceTuripSelectionScreenMode.Turips,
                        placeName = "성수동 카페",
                        isChanged = false,
                        turips =
                            persistentListOf(
                                TuripModel(
                                    id = 1L,
                                    name = "서울 1박2일",
                                    placeCount = 10,
                                    isSelected = true,
                                ),
                                TuripModel(
                                    id = 2L,
                                    name = "경주 1박2일",
                                    placeCount = 2,
                                    isSelected = false,
                                ),
                                TuripModel(
                                    id = 3L,
                                    name = "부산 1박2일",
                                    placeCount = 5,
                                    isSelected = false,
                                ),
                            ),
                        selectedTuripPlaces = persistentListOf(),
                        selectionPlaceId = 0L,
                    ),
                onBackFromTuripDetail = {},
                onDismissRequest = {},
                onAddTuripClick = {},
                onTuripPlaceClickAtTurips = {},
                onNavigateToTurip = { _, _ -> },
                onConfirmClick = {},
                onMapClick = {},
                onTuripPlaceClickAtTuripDetail = {},
                onShareClick = {},
                onDragStart = {},
                onDragPlace = { _, _ -> },
                onDragEnd = {},
            )
        }
    }
}

@Preview(name = "튜립 상세", showBackground = true)
@Composable
private fun PlaceTuripSelectionContentTuripDetailPreview() {
    TuripTheme {
        Surface(color = TuripTheme.colors.white) {
            PlaceTuripSelectionContent(
                uiState =
                    PlaceTuripSelectionUiState(
                        screenMode =
                            PlaceTuripSelectionScreenMode.TuripDetail(
                                turipId = 1L,
                                turipName = "서울 1박2일",
                            ),
                        placeName = "",
                        isChanged = true,
                        turips = persistentListOf(),
                        selectedTuripPlaces =
                            persistentListOf(
                                TuripPlaceModel.Idle.copy(
                                    turipPlaceId = 1L,
                                    placeId = 1L,
                                    order = 1L,
                                    name = "폴바셋",
                                    isTuripPlace = true,
                                    category = "카페",
                                ),
                                TuripPlaceModel.Idle.copy(
                                    turipPlaceId = 2L,
                                    placeId = 2L,
                                    order = 2L,
                                    name = "순두부 찌개 전문점",
                                    isTuripPlace = true,
                                    category = "음식점",
                                ),
                            ),
                        selectionPlaceId = 1L,
                    ),
                onBackFromTuripDetail = {},
                onDismissRequest = {},
                onAddTuripClick = {},
                onTuripPlaceClickAtTurips = {},
                onNavigateToTurip = { _, _ -> },
                onConfirmClick = {},
                onMapClick = {},
                onTuripPlaceClickAtTuripDetail = {},
                onShareClick = {},
                onDragStart = {},
                onDragPlace = { _, _ -> },
                onDragEnd = {},
            )
        }
    }
}
