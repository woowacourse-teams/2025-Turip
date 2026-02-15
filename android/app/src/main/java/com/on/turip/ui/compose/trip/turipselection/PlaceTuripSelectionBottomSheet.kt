package com.on.turip.ui.compose.trip.turipselection

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.common.extensions.dismissAndExecute
import com.on.turip.ui.common.extensions.showSnackbarWithAction
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.trip.model.SelectedPlaceModel
import com.on.turip.ui.compose.trip.turipselection.component.TuripDetail
import com.on.turip.ui.compose.trip.turipselection.component.TuripsContent
import com.on.turip.ui.main.favorite.model.TuripShareModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceTuripSelectionBottomSheet(
    selectedPlaceModel: SelectedPlaceModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToAddTurip: () -> Unit,
    onNavigateToMap: (mapModel: MapModel) -> Unit,
    onShareTurip: (shareModel: TuripShareModel) -> Unit,
    onTuripSelectionConfirm: (placeId: Long, hasTurip: Boolean) -> Unit,
    onDismiss: () -> Unit,
    viewModel: PlaceTuripSelectionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val bottomSheetHeight = with(density) { windowInfo.containerSize.height.toDp() * 0.8f }

    val turipsListState = rememberLazyListState()

    var showLoginSuggestDialog by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(selectedPlaceModel.placeId) {
        viewModel.loadTuripsByPlace(selectedPlaceModel.placeId, selectedPlaceModel.placeName)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect ->
            when (uiEffect) {
                PlaceTuripSelectionUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is PlaceTuripSelectionUiEffect.ShowTuripPlaceRemoveFailed -> {
                    snackbarHostState.showSnackbar(
                        message =
                            resources.getString(
                                R.string.trip_detail_bottom_sheet_snackbar_place_remove_failed,
                                uiEffect.placeName,
                            ),
                        duration = SnackbarDuration.Short,
                    )
                }

                PlaceTuripSelectionUiEffect.TuripShareNotAllowed -> {
                    showLoginSuggestDialog = true
                }

                is PlaceTuripSelectionUiEffect.ShareTurip -> {
                    onShareTurip(uiEffect.turipShareModel)
                }

                is PlaceTuripSelectionUiEffect.ShowTuripPlaceRemoved -> {
                    val messageResource: Int =
                        R.string.trip_detail_bottom_sheet_snackbar_place_removed
                    val actionLabelResource: Int =
                        R.string.trip_detail_bottom_sheet_snackbar_place_remove_undo
                    snackbarHostState.showSnackbarWithAction(
                        message = resources.getString(messageResource, uiEffect.placeName),
                        actionLabel = resources.getString(actionLabelResource),
                        onAction = viewModel::rollbackTuripPlaceDelete,
                        onDismiss = viewModel::commitTuripPlaceDelete,
                    )
                }

                is PlaceTuripSelectionUiEffect.ShowError -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarHostState.showSnackbarWithAction(
                        message = resources.getString(uiModel.titleRes),
                        actionLabel = resources.getString(uiModel.retryTextRes),
                        duration = SnackbarDuration.Long,
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                is PlaceTuripSelectionUiEffect.ShowReorderPlaceFailed -> {
                    val messageResource: Int =
                        R.string.trip_detail_bottom_sheet_snackbar_place_reorder_failed
                    val actionLabelResource: Int =
                        R.string.trip_detail_bottom_sheet_snackbar_place_reorder_retry
                    snackbarHostState.showSnackbarWithAction(
                        message = resources.getString(messageResource),
                        actionLabel = resources.getString(actionLabelResource),
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                is PlaceTuripSelectionUiEffect.UpdateTuripsByPlace -> {
                    onTuripSelectionConfirm(uiEffect.placeId, uiEffect.hasTurip)
                }
            }
        }
    }

    if (showLoginSuggestDialog) {
        TuripDialog(
            title = stringResource(R.string.turip_dialog_login_suggest_title),
            message = stringResource(R.string.turip_dialog_login_suggest_description),
            confirmText = stringResource(R.string.turip_dialog_login_suggest_confirm),
            dismissText = stringResource(R.string.turip_dialog_login_suggest_dismiss),
            onConfirmation = {
                onNavigateToLogin()
                showLoginSuggestDialog = false
            },
            onDismissRequest = { showLoginSuggestDialog = false },
        )
    }

    LaunchedEffect(sheetState.currentValue) {
        snapshotFlow { sheetState.currentValue }
            .distinctUntilChanged()
            .filter { it == SheetValue.Hidden }
            .collect { viewModel.commitTuripPlaceDelete() }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        sheetGesturesEnabled = false,
        dragHandle = null,
        containerColor = TuripTheme.colors.white,
    ) {
        BackHandler {
            when (uiState.screenMode) {
                is PlaceTuripSelectionScreenMode.TuripDetail -> {
                    snackbarHostState.dismissAndExecute { viewModel.onTuripDetailBack() }
                }

                is PlaceTuripSelectionScreenMode.Turips -> {
                    onDismiss()
                }
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(bottomSheetHeight),
        ) {
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
                    CloseButton(onCloseClick = onDismiss, modifier = Modifier.fillMaxWidth())
                }

                when (val mode = uiState.screenMode) {
                    PlaceTuripSelectionScreenMode.Turips -> {
                        TuripsContent(
                            listState = turipsListState,
                            placeName = uiState.placeName,
                            enableConfirm = uiState.isChanged,
                            turips = uiState.turips,
                            onAddTuripClick = onNavigateToAddTurip,
                            onTuripPlaceClick = viewModel::updateTurip,
                            onNavigateToTurip = viewModel::loadPlacesInSelectTurip,
                            onConfirmClick = viewModel::updateTuripsByPlace,
                        )
                    }

                    is PlaceTuripSelectionScreenMode.TuripDetail -> {
                        TuripDetail(
                            turipName = mode.turipName,
                            places = uiState.selectedTuripPlaces,
                            onMapClick = onNavigateToMap,
                            onTuripPlaceClick = {
                                snackbarHostState.dismissAndExecute {
                                    viewModel.applyTuripPlaceDelete(it)
                                }
                            },
                            onBackClick = {
                                snackbarHostState.dismissAndExecute { viewModel.onTuripDetailBack() }
                            },
                            onShareClick = {
                                snackbarHostState.dismissAndExecute { viewModel.shareTurip() }
                            },
                            onDragStart = {
                                snackbarHostState.dismissAndExecute { viewModel.onDragStart() }
                            },
                            onDragPlace = viewModel::onDragMove,
                            onDragEnd = viewModel::onDragEnd,
                        )
                    }
                }
            }
            TuripSnackbar(
                snackbarHostState = snackbarHostState,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = TuripTheme.spacing.medium),
            )
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

@Preview(showBackground = true)
@Composable
private fun PlaceTuripSelectionBottomSheetPreview() {
    val listState = rememberLazyListState()
    TuripTheme {
        Surface(
            color = TuripTheme.colors.primarySub,
            shape = TuripTheme.shape.bottomSheetRounded,
        ) {
            TuripsContent(
                listState = listState,
                placeName = "장소명",
                enableConfirm = false,
                turips = persistentListOf(),
                onAddTuripClick = { },
                onTuripPlaceClick = { },
                onNavigateToTurip = { _, _ -> },
                onConfirmClick = { },
            )
        }
    }
}
