package com.on.turip.ui.compose.turipdetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng
import com.on.turip.R
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.compose.turip.model.TuripTypeModel
import com.on.turip.ui.compose.turipdetail.component.MemberListSheet
import com.on.turip.ui.compose.turipdetail.component.MoreOptionBottomSheet
import com.on.turip.ui.compose.turipdetail.component.TuripInfoRow
import com.on.turip.ui.compose.turipdetail.component.TuripMapContent
import com.on.turip.ui.compose.turipdetail.component.TuripPlaces
import com.on.turip.ui.compose.turipdetail.model.turip.PlaceLatLngUiModel
import com.on.turip.ui.compose.turipdetail.model.turip.TuripShareModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuripDetailScreen(
    selectedTuripId: Long,
    onNavigateToLogin: () -> Unit,
    onShareTuripByText: (turipShareModel: TuripShareModel) -> Unit,
    onShareTuripInvitationLink: (invitationLink: String) -> Unit,
    onNavigateToMap: (map: MapModel) -> Unit,
    onBack: () -> Unit,
    viewModel: TuripDetailViewModel = hiltViewModel(),
) {
    val uiState: TuripDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarDelegate = LocalSnackbarDelegate.current
    val resource = LocalResources.current
    var showLoginSuggestDialog by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = !uiState.showBottomSheet) { onBack() }

    BackHandler {
        scope.launch {
            viewModel.flushDeleteQueueAndAwait()
            onBack()
        }
    }

    LaunchedEffect(selectedTuripId) {
        viewModel.initIfNeeded(selectedTuripId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: TuripDetailUiEffect ->
            when (uiEffect) {
                TuripDetailUiEffect.ShowTuripShareNotAllowed -> {
                    showLoginSuggestDialog = true
                }

                is TuripDetailUiEffect.ShareTuripByText -> {
                    viewModel.dismissBottomSheet()
                    onShareTuripByText(uiEffect.turipShareModel)
                }

                is TuripDetailUiEffect.ShareTuripInvitationLink -> {
                    viewModel.dismissBottomSheet()
                    onShareTuripInvitationLink(uiEffect.invitationLink)
                }

                TuripDetailUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is TuripDetailUiEffect.ShowError -> {
                    val uiModel = uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarDelegate.showSnackbar(
                        message = resource.getString(uiModel.titleRes),
                        actionLabel = resource.getString(uiModel.retryTextRes),
                        duration = SnackbarDuration.Long,
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                TuripDetailUiEffect.TuripDelete -> {
                    viewModel.dismissTuripRemoveDialog()
                    onBack()
                }

                TuripDetailUiEffect.TuripUpdated -> {
                    viewModel.dismissBottomSheet()
                }

                is TuripDetailUiEffect.ShowTuripDetailRemoveFailed -> {
                    snackbarDelegate.showSnackbar(
                        message =
                            resource.getString(
                                R.string.trip_detail_bottom_sheet_snackbar_place_remove_failed,
                                uiEffect.placeName,
                            ),
                        duration = SnackbarDuration.Short,
                    )
                }

                is TuripDetailUiEffect.ShowTuripDetailRemoved -> {
                    snackbarDelegate.showSnackbar(
                        message =
                            resource.getString(
                                R.string.trip_detail_bottom_sheet_snackbar_place_removed,
                                uiEffect.placeName,
                            ),
                        actionLabel =
                            resource.getString(
                                R.string.trip_detail_bottom_sheet_snackbar_place_remove_undo,
                            ),
                        onAction = viewModel::rollbackTuripPlaceDelete,
                        onDismiss = viewModel::commitTuripPlaceDelete,
                    )
                }

                is TuripDetailUiEffect.ShowReorderDetailFailed -> {
                    snackbarDelegate.showSnackbar(
                        message =
                            resource.getString(
                                R.string.trip_detail_bottom_sheet_snackbar_place_reorder_failed,
                            ),
                        actionLabel =
                            resource.getString(
                                R.string.trip_detail_bottom_sheet_snackbar_place_reorder_retry,
                            ),
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                TuripDetailUiEffect.ShowNetworkUnstable -> {
                    snackbarDelegate.showSnackbar(
                        message = "네트워크 오류로 재연결 중입니다...",
                        duration = SnackbarDuration.Short,
                    )
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
                showLoginSuggestDialog = false
                onNavigateToLogin()
            },
            onDismissRequest = { showLoginSuggestDialog = false },
        )
    }

    if (uiState.showBottomSheet) {
        MoreOptionBottomSheet(
            sheetState = modalBottomSheetState,
            isDefault = uiState.selectedTurip.isDefault,
            isTogetherTurip = uiState.selectedTurip.type == TuripTypeModel.TOGETHER,
            onDismiss = viewModel::dismissBottomSheet,
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
        val isTogetherTurip = uiState.selectedTurip.type == TuripTypeModel.TOGETHER

        TuripDialog(
            title =
                stringResource(
                    if (isTogetherTurip) {
                        R.string.bottom_sheet_turip_leave
                    } else {
                        R.string.bottom_sheet_turip_delete
                    },
                ),
            message =
                stringResource(
                    if (isTogetherTurip) {
                        R.string.bottom_sheet_turip_leave_title
                    } else {
                        R.string.bottom_sheet_turip_remove_title
                    },
                    uiState.selectedTurip.name,
                ),
            confirmText =
                stringResource(
                    if (isTogetherTurip) {
                        R.string.bottom_sheet_turip_leave_approve
                    } else {
                        R.string.bottom_sheet_turip_remove_approve
                    },
                ),
            dismissText = stringResource(R.string.bottom_sheet_turip_remove_cancel),
            confirmButtonColor = TuripTheme.colors.error,
            dismissButtonColor = TuripTheme.colors.gray02,
            onConfirmation = {
                viewModel.deleteTurip()
                viewModel.dismissBottomSheet()
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
        modifier = Modifier.fillMaxSize(),
        color = TuripTheme.colors.white,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
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
                        isTogetherTurip = uiState.selectedTurip.type == TuripTypeModel.TOGETHER,
                        selectedTuripId = uiState.selectedTurip.id,
                        selectedTuripName = uiState.selectedTurip.name,
                        turipPlaceModel = uiState.places,
                        navigateToMap = { mapModel: MapModel -> onNavigateToMap(mapModel) },
                        onClickTuripPlace = { placeId: Long ->
                            viewModel.applyTuripPlaceDelete(placeId = placeId)
                        },
                        currentPlaceLatLng = uiState.placesLatLng,
                        onMoreOption = viewModel::showBottomSheet,
                        onBack = onBack,
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
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = TuripTheme.colors.primary)
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
    var currentPlaces: ImmutableList<TuripPlaceModel> by remember(turipPlaceModel) {
        mutableStateOf(turipPlaceModel)
    }

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
            onClickMembers = onClickMembers,
            onClickPlaces = { isMapVisible = false },
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
            places = currentPlaces,
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
                    contentDescription = stringResource(R.string.all_back_description),
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
                        category = "🌰역",
                        isTuripPlace = true,
                    ),
                    TuripPlaceModel.Idle.copy(
                        turipPlaceId = 2L,
                        placeId = 102L,
                        category = "😊명소",
                        name = "북촌한옥마을",
                        isTuripPlace = true,
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
                    latLng = LatLng(1.1, 1.1),
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
