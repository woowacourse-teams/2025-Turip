package com.on.turip.feature.turipdetail.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.component.TuripDialog
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_back_description
import com.on.turip.core.designsystem.generated.resources.all_close_description
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_delete
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_leave
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_leave_approve
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_leave_title
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_remove_approve
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_remove_cancel
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_remove_title
import com.on.turip.core.designsystem.generated.resources.btn_turip_selected
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_snackbar_place_remove_failed
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_snackbar_place_remove_undo
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_snackbar_place_removed
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_snackbar_place_reorder_failed
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_snackbar_place_reorder_retry
import com.on.turip.core.designsystem.generated.resources.turip_detail_sse_reconnected
import com.on.turip.core.designsystem.generated.resources.turip_detail_sse_reconnecting
import com.on.turip.core.designsystem.generated.resources.turip_dialog_login_suggest_confirm
import com.on.turip.core.designsystem.generated.resources.turip_dialog_login_suggest_description
import com.on.turip.core.designsystem.generated.resources.turip_dialog_login_suggest_dismiss
import com.on.turip.core.designsystem.generated.resources.turip_dialog_login_suggest_title
import com.on.turip.core.designsystem.model.SnackbarIconModel
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.turip.TuripType
import com.on.turip.core.ui.component.ErrorScreen
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.toUiModel
import com.on.turip.core.ui.model.turip.TuripShareModel
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.turipdetail.impl.component.MemberListSheet
import com.on.turip.feature.turipdetail.impl.component.MoreOptionBottomSheet
import com.on.turip.feature.turipdetail.impl.component.TuripDetailSkeleton
import com.on.turip.feature.turipdetail.impl.component.TuripInfoRow
import com.on.turip.feature.turipdetail.impl.component.TuripMapContent
import com.on.turip.feature.turipdetail.impl.component.TuripPlaces
import com.on.turip.feature.turipdetail.impl.model.MapModel
import com.on.turip.feature.turipdetail.impl.model.TuripPlaceModel
import com.on.turip.feature.turipdetail.impl.model.turip.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuripDetailScreen(
    selectedTuripId: Long,
    onNavigateToLogin: () -> Unit,
    onShareTuripByText: (turipShareModel: TuripShareModel) -> Unit,
    onShareTuripInvitationLink: (invitationLink: String) -> Unit,
    onNavigateToMap: (map: MapModel) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TuripDetailViewModel = koinViewModel(),
) {
    val uiState: TuripDetailUiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current
    var showLoginSuggestDialog by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val turipSelectedPainter = painterResource(Res.drawable.btn_turip_selected)

    LaunchedEffect(selectedTuripId) {
        viewModel.initIfNeeded(selectedTuripId)
    }

    suspend fun dismissMoreOptionBottomSheet() {
        if (modalBottomSheetState.isVisible) {
            modalBottomSheetState.hide()
        }
        viewModel.dismissMoreOptionBottomSheet()
        delay(250L)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: TuripDetailUiEffect ->
            when (uiEffect) {
                TuripDetailUiEffect.ShowTuripShareNotAllowed -> {
                    showLoginSuggestDialog = true
                }

                is TuripDetailUiEffect.ShareTuripByText -> {
                    dismissMoreOptionBottomSheet()
                    onShareTuripByText(uiEffect.turipShareModel)
                }

                is TuripDetailUiEffect.ShareTuripInvitationLink -> {
                    dismissMoreOptionBottomSheet()
                    onShareTuripInvitationLink(uiEffect.invitationLink)
                }

                TuripDetailUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is TuripDetailUiEffect.ShowError -> {
                    val uiModel = uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarDelegate.showSnackbar(
                        message = getString(uiModel.titleRes),
                        actionLabel = getString(uiModel.retryTextRes),
                        duration = SnackbarDuration.Long,
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                TuripDetailUiEffect.TuripDelete -> {
                    viewModel.dismissTuripRemoveDialog()
                    onBack()
                }

                TuripDetailUiEffect.TuripUpdated -> {
                    viewModel.dismissMoreOptionBottomSheet()
                }

                is TuripDetailUiEffect.ShowTuripDetailRemoveFailed -> {
                    snackbarDelegate.showSnackbar(
                        message =
                            getString(Res.string.trip_detail_bottom_sheet_snackbar_place_remove_failed)
                                .formatResource(uiEffect.placeName),
                        duration = SnackbarDuration.Short,
                    )
                }

                is TuripDetailUiEffect.ShowTuripDetailRemoved -> {
                    snackbarDelegate.showSnackbar(
                        message =
                            getString(Res.string.trip_detail_bottom_sheet_snackbar_place_removed)
                                .formatResource(uiEffect.placeName),
                        actionLabel = getString(Res.string.trip_detail_bottom_sheet_snackbar_place_remove_undo),
                        onAction = viewModel::rollbackTuripPlaceDelete,
                        onDismiss = viewModel::commitTuripPlaceDelete,
                    )
                }

                is TuripDetailUiEffect.ShowReorderDetailFailed -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.trip_detail_bottom_sheet_snackbar_place_reorder_failed),
                        actionLabel = getString(Res.string.trip_detail_bottom_sheet_snackbar_place_reorder_retry),
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                TuripDetailUiEffect.ShowNetworkUnstable -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.turip_detail_sse_reconnecting),
                        duration = SnackbarDuration.Short,
                    )
                }

                TuripDetailUiEffect.ShowNetworkRecovered -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.turip_detail_sse_reconnected),
                        duration = SnackbarDuration.Short,
                        icon = SnackbarIconModel.PainterIcon(turipSelectedPainter),
                        actionLabel = getString(Res.string.all_close_description),
                    )
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
                showLoginSuggestDialog = false
                onNavigateToLogin()
            },
            onDismissRequest = { showLoginSuggestDialog = false },
        )
    }

    if (uiState.showMoreOptionBottomSheet) {
        MoreOptionBottomSheet(
            sheetState = modalBottomSheetState,
            isDefault = uiState.selectedTurip.isDefault,
            isTogetherTurip = uiState.selectedTurip.type == TuripType.TOGETHER,
            onDismiss = viewModel::dismissMoreOptionBottomSheet,
            onShareTuripByTextClick = viewModel::shareTuripByText,
            onShareTuripInvitationLinkClick = viewModel::shareTuripInvitationLink,
            onDeleteClick = viewModel::showTuripRemoveDialog,
            screenMode = uiState.screenMode,
            onScreenModeChange = { screenMode: TuripPlaceScreenMode ->
                viewModel.updateScreenMode(turipPlaceScreenMode = screenMode)
            },
            turipNameStatus = uiState.turipNameStatus,
            turipName = uiState.inputTuripName,
            onNameChanged = viewModel::updateInputName,
            onConfirmClick = viewModel::updateTuripName,
        )
    }

    if (uiState.showTuripRemoveDialog) {
        val isTogetherTurip = uiState.selectedTurip.type == TuripType.TOGETHER
        val title =
            stringResource(if (isTogetherTurip) Res.string.bottom_sheet_turip_leave else Res.string.bottom_sheet_turip_delete)
        val message =
            stringResource(
                if (isTogetherTurip) Res.string.bottom_sheet_turip_leave_title else Res.string.bottom_sheet_turip_remove_title,
            ).formatResource(uiState.selectedTurip.name)
        val confirmText =
            stringResource(
                if (isTogetherTurip) Res.string.bottom_sheet_turip_leave_approve else Res.string.bottom_sheet_turip_remove_approve,
            )

        TuripDialog(
            title = title,
            message = message,
            confirmText = confirmText,
            dismissText = stringResource(Res.string.bottom_sheet_turip_remove_cancel),
            confirmButtonColor = TuripTheme.colors.error,
            dismissButtonColor = TuripTheme.colors.gray02,
            onConfirmation = {
                viewModel.deleteTurip()
                viewModel.dismissMoreOptionBottomSheet()
            },
            onDismissRequest = viewModel::dismissTuripRemoveDialog,
            modifier = Modifier.fillMaxSize(),
        )
    }

    if (uiState.showMemberBottomSheet) {
        MemberListSheet(
            title = uiState.selectedTurip.name,
            nickNames = uiState.members,
            onDismiss = viewModel::dismissMemberBottomSheet,
            sheetState = modalBottomSheetState,
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = TuripTheme.colors.white,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    TuripDetailSkeleton()
                }

                uiState.errorUiState != ErrorUiState.None -> {
                    ErrorContent(
                        errorUiState = uiState.errorUiState,
                        onRetryClick = {
                            viewModel.loadTurip(selectedTuripId)
                            viewModel.loadPlaces(selectedTuripId)
                        },
                    )
                }

                else -> {
                    TuripPlaceContent(
                        nicknames = uiState.members,
                        isTogetherTurip = uiState.selectedTurip.type == TuripType.TOGETHER,
                        selectedTuripId = uiState.selectedTurip.id,
                        selectedTuripName = uiState.selectedTurip.name,
                        turipPlaceModel = uiState.places,
                        navigateToMap = { mapModel: MapModel -> onNavigateToMap(mapModel) },
                        onClickTuripPlace = { placeId: Long ->
                            viewModel.applyTuripPlaceDelete(placeId = placeId)
                        },
                        currentPlaceLatLng = uiState.placesLatLng,
                        onMoreOption = viewModel::showMoreOptionBottomSheet,
                        onBack = {
                            scope.launch {
                                viewModel.flushDeleteQueueAndAwait()
                                viewModel.syncMemberCountToCachedTurips()
                                onBack()
                            }
                        },
                        selectedPlace = uiState.selectedPlace,
                        onItemClick = viewModel::updateSelectedPlace,
                        onDragStart = viewModel::onDragStart,
                        onDragPlace = viewModel::onDragMove,
                        onDragEnd = viewModel::onDragEnd,
                        onClickMembers = viewModel::showMemberBottomSheet,
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    errorUiState: ErrorUiState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ErrorScreen(
        errorUiState = errorUiState,
        onRetryClick = onRetryClick,
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
private fun TuripPlaceContent(
    nicknames: ImmutableList<String>,
    isTogetherTurip: Boolean,
    selectedTuripId: Long,
    selectedTuripName: String,
    selectedPlace: PlaceLatLngUiModel,
    onItemClick: (placeId: Long) -> Unit,
    currentPlaceLatLng: ImmutableList<PlaceLatLngUiModel>,
    turipPlaceModel: ImmutableList<TuripPlaceModel>,
    navigateToMap: (map: MapModel) -> Unit,
    onClickTuripPlace: (placeId: Long) -> Unit,
    onClickMembers: () -> Unit,
    onMoreOption: () -> Unit,
    onDragStart: () -> Unit,
    onDragPlace: (from: Int, to: Int) -> Unit,
    onDragEnd: () -> Unit,
    onBack: () -> Unit,
) {
    var isMapVisible by remember { mutableStateOf(true) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .background(TuripTheme.colors.white),
    ) {
        Header(
            turipName = selectedTuripName,
            onBackClick = onBack,
            onMoreOption = onMoreOption,
        )

        TuripInfoRow(
            savedPlaceCount = currentPlaceLatLng.size,
            participantCount = nicknames.size,
            showParticipant = isTogetherTurip,
            onMembersClick = onClickMembers,
            onPlacesClick = { isMapVisible = false },
            modifier = Modifier.padding(vertical = TuripTheme.spacing.small),
        )

        Spacer(modifier = Modifier.size(TuripTheme.spacing.extraSmall))

        if (currentPlaceLatLng.isNotEmpty()) {
            TuripMapContent(
                selectedTuripId = selectedTuripId,
                selectedPlace = selectedPlace,
                places = currentPlaceLatLng,
                isMapVisible = isMapVisible,
                onMapToggle = { isMapVisible = !isMapVisible },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        TuripPlaces(
            places = turipPlaceModel,
            onItemClick = onItemClick,
            onMapClick = navigateToMap,
            onTuripPlaceClick = onClickTuripPlace,
            onDragStart = onDragStart,
            onDragPlace = onDragPlace,
            onDragEnd = onDragEnd,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun Header(
    turipName: String,
    onBackClick: () -> Unit,
    onMoreOption: () -> Unit,
) {
    TuripAppBar(
        start = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.all_back_description),
                    tint = TuripTheme.colors.gray03,
                )
            }
        },
        center = {
            Text(
                text = turipName,
                style = TuripTheme.typography.title1,
                color = TuripTheme.colors.black,
            )
        },
        end = {
            IconButton(onClick = onMoreOption) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = TuripTheme.colors.gray03,
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun TuripPlaceScreenPreview() {
    TuripTheme {
        TuripPlaceContent(
            nicknames = persistentListOf("안녕", "하세", "요"),
            turipPlaceModel =
                persistentListOf(
                    TuripPlaceModel.Idle.copy(
                        turipPlaceId = 1L,
                        placeId = 101L,
                        name = "안국역",
                        category = "역",
                        isTuripPlace = true,
                        latitude = 1.1,
                        longitude = 1.1,
                    ),
                    TuripPlaceModel.Idle.copy(
                        turipPlaceId = 2L,
                        placeId = 102L,
                        category = "명소",
                        name = "북촌한옥마을",
                        isTuripPlace = true,
                        latitude = 1.2,
                        longitude = 1.2,
                    ),
                ),
            selectedTuripName = "폴더폴더",
            navigateToMap = {},
            onClickTuripPlace = {},
            onMoreOption = {},
            currentPlaceLatLng = persistentListOf(),
            selectedTuripId = -1L,
            onBack = {},
            selectedPlace =
                PlaceLatLngUiModel(
                    placeId = 101L,
                    name = "안국역",
                    latitude = 1.1,
                    longitude = 1.1,
                ),
            onItemClick = {},
            onDragStart = {},
            onDragPlace = { _, _ -> },
            onDragEnd = {},
            onClickMembers = {},
            isTogetherTurip = false,
        )
    }
}
