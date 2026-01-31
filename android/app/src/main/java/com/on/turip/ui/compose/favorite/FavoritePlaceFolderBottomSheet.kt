package com.on.turip.ui.compose.favorite

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.favorite.component.FavoriteFolderDetail
import com.on.turip.ui.compose.favorite.component.FavoriteFoldersContent
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.main.favorite.FavoritePlaceFolderViewModel
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderScreenMode
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderUiEffect
import kotlinx.collections.immutable.persistentListOf

@Composable
fun FavoritePlaceFolderBottomSheet(
    onNavigateToLogin: () -> Unit,
    onNavigateToAddFolder: () -> Unit,
    onNavigateToMap: (mapModel: MapModel) -> Unit,
    onShareFolder: (shareModel: FavoriteFolderShareModel) -> Unit,
    onDismiss: () -> Unit,
    viewModel: FavoritePlaceFolderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val bottomSheetHeight = with(density) { windowInfo.containerSize.height.toDp() * 0.8f }

    val foldersListState = rememberLazyListState()

    var showLoginSuggestDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect ->
            when (uiEffect) {
                FavoritePlaceFolderUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is FavoritePlaceFolderUiEffect.ShowFavoritePlaceRemoveFailed -> {
                    snackbarHostState.showSnackbar(
                        message =
                            context.getString(
                                R.string.bottom_sheet_favorite_place_folder_snackbar_place_remove_failed,
                                uiEffect.placeName,
                            ),
                        duration = SnackbarDuration.Short,
                    )
                }

                FavoritePlaceFolderUiEffect.FolderShareNotAllowed -> {
                    showLoginSuggestDialog = true
                }

                is FavoritePlaceFolderUiEffect.ShareFolder -> {
                    onShareFolder(uiEffect.favoriteFolderShareModel)
                }

                is FavoritePlaceFolderUiEffect.ShowRemovedFavoritePlace -> {
                    val messageResource: Int =
                        R.string.bottom_sheet_favorite_place_folder_snackbar_place_removed
                    val actionLabelResource: Int =
                        R.string.bottom_sheet_favorite_place_folder_snackbar_place_remove_undo
                    val result =
                        snackbarHostState.showSnackbar(
                            message = context.getString(messageResource, uiEffect.placeName),
                            actionLabel = context.getString(actionLabelResource),
                            duration = SnackbarDuration.Short,
                        )
                    when (result) {
                        SnackbarResult.ActionPerformed -> viewModel.rollbackFavoritePlaceDelete()
                        SnackbarResult.Dismissed -> viewModel.commitFavoritePlaceDelete()
                    }
                }

                is FavoritePlaceFolderUiEffect.ShowError -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collect
                    val result =
                        snackbarHostState.showSnackbar(
                            message = context.getString(uiModel.titleRes),
                            actionLabel = context.getString(uiModel.retryTextRes),
                            duration = SnackbarDuration.Long,
                        )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.handleErrorRetryRequest(uiEffect.retryAction)
                    }
                }
            }
        }
    }

    BackHandler {
        when (uiState.screenMode) {
            is FavoritePlaceFolderScreenMode.FolderDetail -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                viewModel.onFavoriteDetailBack()
            }

            is FavoritePlaceFolderScreenMode.Folders -> {
                onDismiss()
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
        modifier =
            Modifier
                .fillMaxWidth()
                .height(bottomSheetHeight),
        shape = TuripTheme.shape.bottomSheetRounded,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .padding(top = TuripTheme.spacing.medium)
                        .navigationBarsPadding(),
            ) {
                AnimatedVisibility(
                    visible = uiState.screenMode is FavoritePlaceFolderScreenMode.Folders,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    CloseButton(onCloseClick = onDismiss, modifier = Modifier.fillMaxWidth())
                }

                when (val mode = uiState.screenMode) {
                    FavoritePlaceFolderScreenMode.Folders -> {
                        FavoriteFoldersContent(
                            listState = foldersListState,
                            placeName = uiState.placeName,
                            enableConfirm = uiState.isChanged,
                            folders = uiState.favoritePlaceFolders,
                            onAddFolderClick = onNavigateToAddFolder,
                            onFavoriteClick = viewModel::updateFolder,
                            onNavigateToFolder = viewModel::loadPlacesInSelectFolder,
                            onConfirmClick = {
                                // TODO : 폴더 전반에 대한 찜 업데이트 API 연동 예정
                                // TODO : API 성공 -> 스낵바 + dismiss, 실패 -> 스낵바로 안내
                            },
                        )
                    }

                    is FavoritePlaceFolderScreenMode.FolderDetail -> {
                        FavoriteFolderDetail(
                            folderName = mode.folderName,
                            places = uiState.selectedFolderPlaces,
                            onMapClick = onNavigateToMap,
                            onFavoriteClick = viewModel::applyFavoritePlaceDelete,
                            onBackClick = {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                viewModel.onFavoriteDetailBack()
                            },
                            onShareClick = viewModel::shareFolder,
                            onDragStart = viewModel::onDragStart,
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
                    .size(40.dp),
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
private fun FavoritePlaceFolderBottomSheetPreview() {
    val listState = rememberLazyListState()
    TuripTheme {
        Surface(
            color = TuripTheme.colors.primarySub,
            shape = TuripTheme.shape.bottomSheetRounded,
        ) {
            FavoriteFoldersContent(
                listState = listState,
                placeName = "장소명",
                enableConfirm = false,
                folders = persistentListOf(),
                onAddFolderClick = { },
                onFavoriteClick = { },
                onNavigateToFolder = { _, _ -> },
                onConfirmClick = { },
            )
        }
    }
}
