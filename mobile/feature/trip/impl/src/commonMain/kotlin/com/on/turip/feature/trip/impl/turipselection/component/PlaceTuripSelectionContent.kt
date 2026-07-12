package com.on.turip.feature.trip.impl.turipselection.component

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_close_description
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.trip.impl.model.MapModel
import com.on.turip.feature.trip.impl.turipselection.PlaceTuripSelectionScreenMode
import com.on.turip.feature.trip.impl.turipselection.PlaceTuripSelectionUiState
import com.on.turip.feature.trip.impl.turipselection.TuripSelectionModel
import com.on.turip.feature.trip.impl.turipselection.model.TuripPlaceModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlaceTuripSelectionContent(
    uiState: PlaceTuripSelectionUiState,
    onBackFromTuripDetail: () -> Unit,
    onDismissRequest: () -> Unit,
    onAddTuripClick: () -> Unit,
    onTuripPlaceClickAtTurips: (turipModel: TuripSelectionModel) -> Unit,
    onNavigateToTurip: (turipId: Long) -> Unit,
    onConfirmClick: () -> Unit,
    onMapClick: (mapModel: MapModel) -> Unit,
    onTuripPlaceClickAtTuripDetail: (place: TuripPlaceModel) -> Unit,
    onShareClick: () -> Unit,
    onDragStart: () -> Unit,
    onDragPlace: (from: Int, to: Int) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                        enableConfirm = uiState.isChanged && !uiState.isUpdatingTurips,
                        turips = uiState.turips,
                        onAddTuripClick = onAddTuripClick,
                        onTuripPlaceClick = onTuripPlaceClickAtTurips,
                        onNavigateToTurip = onNavigateToTurip,
                        onConfirmClick = onConfirmClick,
                    )
                }

                is PlaceTuripSelectionScreenMode.TuripDetail -> {
                    TuripDetail(
                        turipName = mode.turipModel.name,
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
                contentDescription = stringResource(Res.string.all_close_description),
                modifier = Modifier.size(16.dp),
                tint = TuripTheme.colors.gray04,
            )
        }
    }
}
