package com.on.turip.ui.compose.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.on.turip.R
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.common.extensions.showSnackbarWithAction
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.turip.selection.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.model.TuripPlaceUiEffect
import com.on.turip.ui.main.favorite.model.TuripShareModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TuripPlaceScreen(
    selectedTuripId: Long,
    onNavigateToLogin: () -> Unit = {},
    onShareTurip: (TuripShareModel) -> Unit = {},
    onNavigateToMap: () -> Unit,
    viewModel: TuripPlaceViewModel = hiltViewModel(),
) {
    val uiState: TuripPlaceUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resource = LocalResources.current
    var showLoginSuggestDialog by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
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
                        onRetryClick = { viewModel.loadPlaces(selectedTuripId) },
                    )
                }

                else -> {
                    TuripPlaceContent(
                        turipPlaceModel = uiState.places,
                        navigateToMap = { _: MapModel -> onNavigateToMap() },
                        onClickTuripPlace = { placeId: Long ->
                            viewModel.updateTuripPlace(placeId = placeId, isTuripPlace = true)
                        },
                        onUpdateTuripPlacesOrder = viewModel::updateTuripPlacesOrder,
                        onShareClick = { viewModel.shareTurip() },
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
    turipPlaceModel: ImmutableList<TuripPlaceModel>,
    navigateToMap: (map: MapModel) -> Unit,
    onClickTuripPlace: (placeId: Long) -> Unit,
    onShareClick: () -> Unit,
    onUpdateTuripPlacesOrder: (updatePlaces: ImmutableList<TuripPlaceModel>) -> Unit,
) {
    var currentPlaces: ImmutableList<TuripPlaceModel> by remember(turipPlaceModel) {
        mutableStateOf(turipPlaceModel)
    }
    var dragStartPlaces: ImmutableList<TuripPlaceModel> by remember(turipPlaceModel) {
        mutableStateOf(turipPlaceModel)
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white),
    ) {
        TuripDetail(
            places = currentPlaces,
            onMapClick = navigateToMap,
            onTuripPlaceClick = onClickTuripPlace,
            onDragStart = {
                dragStartPlaces = currentPlaces
            },
            onDragPlace = { from: Int, to: Int ->
                if (from == to) return@TuripDetail
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
            modifier = Modifier.fillMaxSize(),
            onShareClick = onShareClick,
        )
    }
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
            navigateToMap = {},
            onClickTuripPlace = {},
            onShareClick = {},
            onUpdateTuripPlacesOrder = {},
        )
    }
}
