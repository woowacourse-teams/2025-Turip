package com.on.turip.feature.trip.impl.turipselection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import com.on.turip.core.designsystem.component.TuripDialog
import com.on.turip.core.designsystem.component.TuripSnackbar
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_snackbar_place_remove_failed
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_snackbar_place_remove_undo
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_snackbar_place_removed
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_snackbar_place_reorder_failed
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_snackbar_place_reorder_retry
import com.on.turip.core.designsystem.generated.resources.turip_dialog_login_suggest_confirm
import com.on.turip.core.designsystem.generated.resources.turip_dialog_login_suggest_description
import com.on.turip.core.designsystem.generated.resources.turip_dialog_login_suggest_dismiss
import com.on.turip.core.designsystem.generated.resources.turip_dialog_login_suggest_title
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.error.toUiModel
import com.on.turip.core.ui.model.turip.TuripShareModel
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.trip.impl.model.MapModel
import com.on.turip.feature.trip.impl.model.SelectedPlaceModel
import com.on.turip.feature.trip.impl.turipselection.component.PlaceTuripSelectionContent
import com.on.turip.feature.trip.impl.turipselection.component.ShareOptionBottomSheet
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
    onNavigateToTuripDetail: (turipId: Long) -> Unit,
    onNavigateToMap: (mapModel: MapModel) -> Unit,
    onShareTuripByText: (shareModel: TuripShareModel) -> Unit,
    onShareTuripInvitationLink: (invitationLink: String) -> Unit,
    onPlaceTuripChanged: (placeId: Long, hasTurip: Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaceTuripSelectionViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current

    var showLoginSuggestDialog by remember { mutableStateOf(false) }
    var showShareOptionSheet by remember { mutableStateOf(false) }
    val shareOptionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val placeId = selectedPlaceModel.placeId
    val placeName = selectedPlaceModel.placeName
    val isTuripsMode = uiState.screenMode is PlaceTuripSelectionScreenMode.Turips

    LaunchedEffect(placeId, placeName, isTuripsMode) {
        if (isTuripsMode) {
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
                            getString(Res.string.trip_detail_bottom_sheet_snackbar_place_remove_failed)
                                .formatResource(uiEffect.placeName),
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
                    snackbarDelegate.showSnackbar(
                        message =
                            getString(Res.string.trip_detail_bottom_sheet_snackbar_place_removed)
                                .formatResource(uiEffect.placeName),
                        actionLabel = getString(Res.string.trip_detail_bottom_sheet_snackbar_place_remove_undo),
                        onAction = viewModel::rollbackTuripPlaceDelete,
                        onDismiss = viewModel::commitTuripPlaceDelete,
                    )
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

                is PlaceTuripSelectionUiEffect.ShowReorderPlaceFailed -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.trip_detail_bottom_sheet_snackbar_place_reorder_failed),
                        actionLabel = getString(Res.string.trip_detail_bottom_sheet_snackbar_place_reorder_retry),
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
            title = stringResource(Res.string.turip_dialog_login_suggest_title),
            message = stringResource(Res.string.turip_dialog_login_suggest_description),
            confirmText = stringResource(Res.string.turip_dialog_login_suggest_confirm),
            dismissText = stringResource(Res.string.turip_dialog_login_suggest_dismiss),
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
        modifier = modifier,
    ) {
        val windowInfo = LocalWindowInfo.current
        val density = LocalDensity.current
        val bottomSheetHeight = with(density) { windowInfo.containerSize.height.toDp() * 0.8f }

        Box(modifier = Modifier.fillMaxWidth().height(bottomSheetHeight)) {
            PlaceTuripSelectionContent(
                uiState = uiState,
                onBackFromTuripDetail = viewModel::onTuripDetailBack,
                onDismissRequest = viewModel::requestDismiss,
                onAddTuripClick = onNavigateToAddTurip,
                onTuripPlaceClickAtTurips = viewModel::updateTurip,
                onNavigateToTurip = onNavigateToTuripDetail,
                onConfirmClick = viewModel::updateTuripsByPlace,
                onMapClick = onNavigateToMap,
                onTuripPlaceClickAtTuripDetail = viewModel::applyTuripPlaceDelete,
                onShareClick = { showShareOptionSheet = true },
                onDragStart = viewModel::onDragStart,
                onDragPlace = viewModel::onDragMove,
                onDragEnd = viewModel::onDragEnd,
                modifier = Modifier.fillMaxWidth(),
            )
            TuripSnackbar(
                snackbarHostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            )
        }
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
