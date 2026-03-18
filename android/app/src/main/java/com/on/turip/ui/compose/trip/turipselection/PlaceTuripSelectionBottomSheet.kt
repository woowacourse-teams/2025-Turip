package com.on.turip.ui.compose.trip.turipselection

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.on.turip.R
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.trip.model.SelectedPlaceModel
import com.on.turip.ui.compose.trip.turipselection.component.PlaceTuripSelectionContent
import com.on.turip.ui.compose.trip.turipselection.component.ShareOptionBottomSheet
import com.on.turip.ui.compose.trip.turipselection.component.TuripsContent
import com.on.turip.ui.compose.turipdetail.model.turip.TuripShareModel
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceTuripSelectionBottomSheet(
    sheetState: SheetState,
    selectedPlaceModel: SelectedPlaceModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToAddTurip: () -> Unit,
    onNavigateToMap: (mapModel: MapModel) -> Unit,
    onShareTuripByText: (shareModel: TuripShareModel) -> Unit,
    onShareTuripInvitationLink: (invitationLink: String) -> Unit,
    onPlaceTuripChanged: (placeId: Long, hasTurip: Boolean) -> Unit,
    onDismiss: () -> Unit,
    viewModel: PlaceTuripSelectionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarDelegate = LocalSnackbarDelegate.current
    val resources = LocalResources.current

    var showLoginSuggestDialog by remember { mutableStateOf(false) }
    var showShareOptionSheet by remember { mutableStateOf(false) }
    val shareOptionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val lifecycleOwner = LocalLifecycleOwner.current
    val placeId = selectedPlaceModel.placeId
    val placeName = selectedPlaceModel.placeName
    val isTuripsMode = uiState.screenMode is PlaceTuripSelectionScreenMode.Turips
    LaunchedEffect(lifecycleOwner, placeId, isTuripsMode) {
        if (!isTuripsMode) return@LaunchedEffect
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadTuripsByPlace(placeId, placeName)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect ->
            when (uiEffect) {
                PlaceTuripSelectionUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is PlaceTuripSelectionUiEffect.ShowTuripPlaceRemoveFailed -> {
                    snackbarDelegate.showSnackbar(
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

                is PlaceTuripSelectionUiEffect.ShareTuripByText -> {
                    onShareTuripByText(uiEffect.turipShareModel)
                }

                is PlaceTuripSelectionUiEffect.ShareTuripInvitationLink -> {
                    onShareTuripInvitationLink(uiEffect.invitationLink)
                }

                is PlaceTuripSelectionUiEffect.ShowTuripPlaceRemoved -> {
                    val messageResource: Int =
                        R.string.trip_detail_bottom_sheet_snackbar_place_removed
                    val actionLabelResource: Int =
                        R.string.trip_detail_bottom_sheet_snackbar_place_remove_undo
                    snackbarDelegate.showSnackbar(
                        message = resources.getString(messageResource, uiEffect.placeName),
                        actionLabel = resources.getString(actionLabelResource),
                        onAction = viewModel::rollbackTuripPlaceDelete,
                        onDismiss = viewModel::commitTuripPlaceDelete,
                    )
                }

                is PlaceTuripSelectionUiEffect.ShowError -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarDelegate.showSnackbar(
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
                    snackbarDelegate.showSnackbar(
                        message = resources.getString(messageResource),
                        actionLabel = resources.getString(actionLabelResource),
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                is PlaceTuripSelectionUiEffect.UpdateTuripsByPlace -> {
                    onPlaceTuripChanged(uiEffect.placeId, uiEffect.hasTurip)
                }

                is PlaceTuripSelectionUiEffect.HasNoTuripsByPlace -> {
                    onPlaceTuripChanged(uiEffect.placeId, false)
                }

                PlaceTuripSelectionUiEffect.Dismiss -> {
                    onDismiss()
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

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { viewModel.requestDismiss() },
        sheetGesturesEnabled = false,
        dragHandle = null,
        containerColor = TuripTheme.colors.white,
    ) {
        val windowInfo = LocalWindowInfo.current
        val density = LocalDensity.current
        val bottomSheetHeight = with(density) { windowInfo.containerSize.height.toDp() * 0.8f }

        PlaceTuripSelectionContent(
            uiState = uiState,
            onBackFromTuripDetail = viewModel::onTuripDetailBack,
            onDismissRequest = viewModel::requestDismiss,
            onAddTuripClick = onNavigateToAddTurip,
            onTuripPlaceClickAtTurips = viewModel::updateTurip,
            onNavigateToTurip = viewModel::loadPlacesInSelectTurip,
            onConfirmClick = viewModel::updateTuripsByPlace,
            onMapClick = onNavigateToMap,
            onTuripPlaceClickAtTuripDetail = viewModel::applyTuripPlaceDelete,
            onShareClick = { showShareOptionSheet = true },
            onDragStart = viewModel::onDragStart,
            onDragPlace = viewModel::onDragMove,
            onDragEnd = viewModel::onDragEnd,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(bottomSheetHeight),
        )
    }

    if (showShareOptionSheet) {
        val isInviteLinkEnabled =
            (uiState.screenMode as? PlaceTuripSelectionScreenMode.TuripDetail)?.turipModel?.isDefault == false

        ShareOptionBottomSheet(
            sheetState = shareOptionSheetState,
            onDismiss = { showShareOptionSheet = false },
            onShareByTextClick = {
                showShareOptionSheet = false
                viewModel.shareTuripByText()
            },
            onInviteLinkClick = {
                showShareOptionSheet = false
                viewModel.shareTuripInvitationLink()
            },
            isInviteLinkEnabled = isInviteLinkEnabled,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceTuripSelectionBottomSheetPreview() {
    TuripTheme {
        TuripsContent(
            placeName = "장소명",
            enableConfirm = false,
            turips = persistentListOf(),
            onAddTuripClick = { },
            onTuripPlaceClick = { },
            onNavigateToTurip = { },
            onConfirmClick = { },
        )
    }
}
