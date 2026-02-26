package com.on.turip.ui.compose.favorite

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.model.LatLng
import com.on.turip.R
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.common.extensions.showSnackbarWithAction
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.favorite.component.MoreOptionBottomSheet
import com.on.turip.ui.compose.favorite.component.TuripMapContent
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.model.PlaceLatLngUiModel
import com.on.turip.ui.main.favorite.model.TuripPlaceUiEffect
import com.on.turip.ui.main.favorite.model.TuripShareModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuripDetailScreen(
    selectedTuripId: Long,
    onNavigateToLogin: () -> Unit,
    onShareTurip: (turipShareModel: TuripShareModel) -> Unit,
    onNavigateToMap: (uri: Uri) -> Unit,
    goBack: () -> Unit,
    viewModel: TuripPlaceViewModel = hiltViewModel(),
) {
    val uiState: TuripPlaceUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resource = LocalResources.current
    var showLoginSuggestDialog by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var dialogState: Boolean by remember { mutableStateOf(false) }

    BackHandler { goBack() }

    DisposableEffect(Unit) {
        onDispose { viewModel.resetUiState() }
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadSelectedTurip(selectedTuripId)
            viewModel.loadPlaces(selectedTuripId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: TuripPlaceUiEffect ->
            when (uiEffect) {
                TuripPlaceUiEffect.ShowTuripShareNotAllowed -> {
                    showLoginSuggestDialog = true
                }

                is TuripPlaceUiEffect.ShareTurip -> {
                    onShareTurip(uiEffect.turipShareModel)
                }

                TuripPlaceUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is TuripPlaceUiEffect.ShowError -> {
                    val uiModel = uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarHostState.showSnackbarWithAction(
                        message = resource.getString(uiModel.titleRes),
                        actionLabel = resource.getString(uiModel.retryTextRes),
                        duration = SnackbarDuration.Long,
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                TuripPlaceUiEffect.TuripDelete -> {
                    dialogState = false
                    goBack()
                }

                TuripPlaceUiEffect.TuripUpdated -> {
                    viewModel.dismissBottomSheet()
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

    if (uiState.showBottomSheet) {
        MoreOptionBottomSheet(
            sheetState = modalBottomSheetState,
            isDefault = uiState.selectedTurip.isDefault,
            onDismiss = { viewModel.dismissBottomSheet() },
            onShareClick = viewModel::shareTurip,
            onInviteLinkClick = {},
            onDeleteClick = { dialogState = true },
            screenMode = uiState.screenMode,
            onScreenModeChange = { screenMode: TuripPlaceScreenMode ->
                viewModel.updateScreenMode(screenMode)
            },
            turipNameStatus = uiState.turipNameStatus,
            folderName = uiState.inputTuripName,
            onNameChanged = viewModel::updateInputName,
            onConfirmClick = viewModel::updateTuripName,
        )
    }

    if (dialogState) {
        TuripDialog(
            title = stringResource(R.string.bottom_sheet_turip_delete),
            message =
                stringResource(
                    R.string.bottom_sheet_turip_remove_title,
                    uiState.selectedTurip.name,
                ),
            confirmText = stringResource(R.string.bottom_sheet_turip_remove_approve),
            dismissText = stringResource(R.string.bottom_sheet_turip_remove_cancel),
            confirmButtonColor = TuripTheme.colors.error,
            dismissButtonColor = TuripTheme.colors.gray02,
            onConfirmation = {
                viewModel.deleteTurip(selectedTuripId)
                viewModel.dismissBottomSheet()
            },
            onDismissRequest = { dialogState = false },
            modifier = Modifier.fillMaxSize(),
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
                            viewModel.loadSelectedTurip(selectedTuripId)
                            viewModel.loadPlaces(selectedTuripId)
                        },
                    )
                }

                else -> {
                    TuripPlaceContent(
                        selectedTuripId = uiState.selectedTurip.id,
                        selectedTuripName = uiState.selectedTurip.name,
                        turipPlaceModel = uiState.places,
                        navigateToMap = { mapModel: MapModel -> onNavigateToMap(mapModel.uri) },
                        onClickTuripPlace = { placeId: Long ->
                            viewModel.updateTuripPlace(placeId = placeId, isTuripPlace = true)
                        },
                        onUpdateTuripPlacesOrder = viewModel::updateTuripPlacesOrder,
                        currentPlaceLatLng = uiState.placesLatLng,
                        onMoreOption = viewModel::showBottomSheet,
                        goBack = goBack,
                        selectedPlace = uiState.selectedPlace,
                        onItemClick = viewModel::updateSelectedPlace,
                    )
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
    selectedTuripId: Long,
    selectedTuripName: String,
    selectedPlace: PlaceLatLngUiModel,
    onItemClick: (placeId: Long) -> Unit,
    currentPlaceLatLng: ImmutableList<PlaceLatLngUiModel>,
    turipPlaceModel: ImmutableList<TuripPlaceModel>,
    navigateToMap: (map: MapModel) -> Unit,
    onClickTuripPlace: (placeId: Long) -> Unit,
    onMoreOption: () -> Unit,
    goBack: () -> Unit,
    onUpdateTuripPlacesOrder: (updatePlaces: ImmutableList<TuripPlaceModel>) -> Unit,
) {
    var currentPlaces: ImmutableList<TuripPlaceModel> by remember(turipPlaceModel) {
        mutableStateOf(turipPlaceModel)
    }
    var dragStartPlaces: ImmutableList<TuripPlaceModel> by remember(turipPlaceModel) {
        mutableStateOf(turipPlaceModel)
    }

    var isMapVisible by remember { mutableStateOf(true) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white),
    ) {
        Header(
            turipName = selectedTuripName,
            onBackClick = goBack,
            onMoreOption = onMoreOption,
        )

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
            onDragStart = { dragStartPlaces = currentPlaces },
            onDragPlace = { from: Int, to: Int ->
                if (from == to) return@TuripPlaces
                currentPlaces =
                    currentPlaces
                        .toMutableList()
                        .apply { add(to, removeAt(from)) }
                        .toImmutableList()
            },
            onDragEnd = {
                if (currentPlaces != dragStartPlaces) {
                    onUpdateTuripPlacesOrder(currentPlaces)
                }
            },
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
            onUpdateTuripPlacesOrder = {},
            currentPlaceLatLng = persistentListOf(),
            selectedTuripId = -1L,
            goBack = {},
            selectedPlace =
                PlaceLatLngUiModel(
                    placeId = 101L,
                    name = "안국역",
                    latLng = LatLng(1.1, 1.1),
                ),
            onItemClick = {},
        )
    }
}
