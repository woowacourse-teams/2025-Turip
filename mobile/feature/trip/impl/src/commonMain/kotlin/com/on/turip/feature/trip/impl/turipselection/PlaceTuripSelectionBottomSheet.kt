package com.on.turip.feature.trip.impl.turipselection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripSnackbarVisuals
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_total_place_count
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_add_title
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_add_turip_description
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_turip_selection_confirm
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_turip_selection_title
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_turip_place_select_description
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_turip_place_unselect_description
import com.on.turip.core.designsystem.generated.resources.turip_added_snackbar_message
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.NameEditorSheetContent
import com.on.turip.core.ui.error.toUiModel
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.trip.impl.model.SelectedPlaceModel
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceTuripSelectionBottomSheet(
    sheetState: SheetState,
    selectedPlaceModel: SelectedPlaceModel,
    snackbarHostState: SnackbarHostState,
    onNavigateToLogin: () -> Unit,
    onNavigateToAddTurip: () -> Unit,
    onPlaceTuripChanged: (placeId: Long, hasTurip: Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaceTuripSelectionViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current
    val addTuripSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(selectedPlaceModel.placeId, selectedPlaceModel.placeName) {
        viewModel.loadTuripsByPlace(selectedPlaceModel.placeId, selectedPlaceModel.placeName)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect ->
            when (uiEffect) {
                PlaceTuripSelectionUiEffect.NavigateToLogin -> onNavigateToLogin()
                PlaceTuripSelectionUiEffect.Dismiss -> onDismiss()
                is PlaceTuripSelectionUiEffect.UpdateTuripsByPlace -> {
                    onPlaceTuripChanged(uiEffect.placeId, uiEffect.hasTurip)
                }

                is PlaceTuripSelectionUiEffect.ShowError -> {
                    val uiModel = uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarDelegate.showSnackbar(
                        message = getString(uiModel.titleRes),
                        actionLabel = getString(uiModel.retryTextRes),
                        duration = SnackbarDuration.Long,
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                is PlaceTuripSelectionUiEffect.TuripAdded -> {
                    viewModel.dismissAddTuripBottomSheet()
                    snackbarHostState.showSnackbar(
                        visuals =
                            TuripSnackbarVisuals(
                                message =
                                    getString(Res.string.turip_added_snackbar_message)
                                        .formatResource(uiEffect.turipName),
                            ),
                    )
                }
            }
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = TuripTheme.colors.white,
        modifier = modifier,
    ) {
        PlaceTuripSelectionContent(
            placeName = selectedPlaceModel.placeName,
            isLoading = uiState.isLoading,
            turips = uiState.turips,
            enableConfirm = uiState.isChanged,
            onAddTuripClick = {
                onNavigateToAddTurip()
                viewModel.showAddTuripBottomSheet()
            },
            onTuripClick = viewModel::updateTurip,
            onConfirmClick = viewModel::updateTuripsByPlace,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (uiState.showAddTuripBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = viewModel::dismissAddTuripBottomSheet,
            sheetState = addTuripSheetState,
            containerColor = TuripTheme.colors.white,
        ) {
            val focusRequester = remember { FocusRequester() }
            NameEditorSheetContent(
                title = stringResource(Res.string.bottom_sheet_turip_add_title),
                turipName = uiState.addTuripInputName,
                turipNameStatus = uiState.addTuripNameStatus,
                isConfirmEnabled = uiState.addTuripNameStatus.isConfirmEnabled && !uiState.isCreatingTurip,
                onNameChanged = viewModel::updateAddTuripInputName,
                onConfirmClick = viewModel::addTurip,
                focusRequester = focusRequester,
                modifier = Modifier.imePadding(),
            )
        }
    }
}

@Composable
private fun PlaceTuripSelectionContent(
    placeName: String,
    isLoading: Boolean,
    turips: ImmutableList<TuripSelectionModel>,
    enableConfirm: Boolean,
    onAddTuripClick: () -> Unit,
    onTuripClick: (TuripSelectionModel) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier.padding(
                start = TuripTheme.spacing.extraLarge,
                end = TuripTheme.spacing.extraLarge,
                bottom = TuripTheme.spacing.extraLarge,
            ),
    ) {
        Text(
            text = stringResource(Res.string.trip_detail_bottom_sheet_turip_selection_title),
            style = TuripTheme.typography.title1,
            color = TuripTheme.colors.gray05,
        )
        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))
        Text(
            text = placeName,
            style = TuripTheme.typography.body2,
            color = TuripTheme.colors.gray04,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        if (isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = TuripTheme.colors.primary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(260.dp),
            ) {
                items(turips, key = { it.id }) { turip ->
                    TuripSelectionItem(
                        turip = turip,
                        onClick = { onTuripClick(turip) },
                    )
                }
            }
        }

        TextButton(
            onClick = onAddTuripClick,
            modifier = Modifier.align(Alignment.Start),
        ) {
            Text(text = stringResource(Res.string.trip_detail_bottom_sheet_add_turip_description))
        }

        Button(
            onClick = onConfirmClick,
            enabled = enableConfirm,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(Res.string.trip_detail_bottom_sheet_turip_selection_confirm))
        }
    }
}

@Composable
private fun TuripSelectionItem(
    turip: TuripSelectionModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = TuripTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = turip.name,
                style = TuripTheme.typography.body1,
                color = TuripTheme.colors.gray05,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(Res.string.all_total_place_count).formatResource(turip.placeCount),
                style = TuripTheme.typography.info1,
                color = TuripTheme.colors.gray03,
            )
        }
        Text(
            text =
                stringResource(
                    if (turip.isSelected) {
                        Res.string.trip_detail_bottom_sheet_turip_place_unselect_description
                    } else {
                        Res.string.trip_detail_bottom_sheet_turip_place_select_description
                    },
                ),
            style = TuripTheme.typography.body2,
            color = if (turip.isSelected) TuripTheme.colors.primary else TuripTheme.colors.gray03,
        )
    }
}
