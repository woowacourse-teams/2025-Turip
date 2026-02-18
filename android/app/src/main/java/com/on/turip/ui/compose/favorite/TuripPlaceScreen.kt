package com.on.turip.ui.compose.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.on.turip.ui.main.favorite.model.TuripModel
import com.on.turip.ui.main.favorite.model.TuripPlaceUiEffect
import com.on.turip.ui.main.favorite.model.TuripShareModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TuripPlaceScreen(
    onNavigateToLogin: () -> Unit = {},
    onShareTurip: (TuripShareModel) -> Unit = {},
    onNavigateToMap: () -> Unit,
    onManageFolderClick: () -> Unit,
    viewModel: TuripPlaceViewModel = hiltViewModel(),
) {
    val uiState: TuripPlaceUiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val resource = LocalResources.current
    var showLoginSuggestDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadTuripsAndPlaces()
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
                        onRetryClick = { viewModel.loadTuripsAndPlaces() },
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
                        turips = uiState.turips,
                        onFolderClick = { folderId: Long ->
                            viewModel.updateTuripWithPlaces(turipId = folderId)
                        },
                        onManageFolderClick = onManageFolderClick,
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
    turips: List<TuripModel>,
    onFolderClick: (folderId: Long) -> Unit,
    onManageFolderClick: () -> Unit,
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
        modifier = Modifier.fillMaxSize().background(TuripTheme.colors.white)
    ) {
        FolderTopBar(
            turips = turips,
            onFolderClick = onFolderClick,
            onManageFolderClick = onManageFolderClick,
        )

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

@Composable
private fun FolderTopBar(
    turips: List<TuripModel>,
    onFolderClick: (folderId: Long) -> Unit,
    onManageFolderClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        ) {
            items(items = turips, key = { it.id }) { turip ->
                val backgroundColor =
                    if (turip.isSelected) TuripTheme.colors.primary else TuripTheme.colors.primarySub
                val textColor =
                    if (turip.isSelected) TuripTheme.colors.white else TuripTheme.colors.black

                Text(
                    text = turip.name,
                    style = TuripTheme.typography.body2,
                    color = textColor,
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(backgroundColor)
                            .clickable { onFolderClick(turip.id) }
                            .padding(horizontal = TuripTheme.spacing.extraLarge, vertical = 10.dp),
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .padding(start = TuripTheme.spacing.extraSmall, end = TuripTheme.spacing.medium)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TuripTheme.colors.gray01)
                    .clickable(onClick = onManageFolderClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_folder_setting),
                contentDescription = null,
                tint = TuripTheme.colors.white,
                modifier = Modifier.size(TuripTheme.spacing.extraLarge),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TuripPlaceScreenPreview() {
    TuripTheme {
        TuripPlaceContent(
            turips =
                listOf(
                    TuripModel(id = 1L, name = "기본 튜립", placeCount = 2, isSelected = true),
                    TuripModel(id = 2L, name = "서울", placeCount = 5, isSelected = false),
                ),
            onFolderClick = {},
            onManageFolderClick = {},
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
