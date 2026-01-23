package com.on.turip.ui.compose.favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.favorite.component.FoldersContent
import com.on.turip.ui.main.favorite.FavoritePlaceFolderViewModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderUiEffect
import kotlinx.collections.immutable.persistentListOf

@Composable
fun FavoritePlaceFolderBottomSheet(
    onNavigateToLogin: () -> Unit,
    onNavigateToAddFolder: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: FavoritePlaceFolderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val maxHeightDp = with(density) { windowInfo.containerSize.height.toDp() * 0.8f }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect ->
            when (uiEffect) {
                FavoritePlaceFolderUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is FavoritePlaceFolderUiEffect.ShowError -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collect
                    val result =
                        snackbarHostState.showSnackbar(
                            message = context.getString(uiModel.titleRes),
                            actionLabel = context.getString(uiModel.retryTextRes),
                            duration = SnackbarDuration.Indefinite,
                        )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.handleErrorRetryRequest(uiEffect.retryAction)
                    }
                }
            }
        }
    }

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeightDp),
        shape = TuripTheme.shape.bottomSheetRounded,
    ) {
        Column(modifier = Modifier.padding(top = TuripTheme.spacing.medium)) {
            FoldersContent(
                placeName = uiState.placeName,
                enableConfirm = uiState.isChanged,
                folders = uiState.favoritePlaceFolders,
                onAddFolderClick = onNavigateToAddFolder,
                onFavoriteClick = viewModel::updateFolder,
                onNavigateToFolder = {}, // TODO : uistate 전환 필요
                onConfirmClick = {
                    // TODO : 폴더 전반에 대한 찜 업데이트 API 연동 예정
                    // TODO : API 성공 -> 스낵바 + dismiss, 실패 -> 스낵바로 안내
                },
            )

            TuripSnackbar(snackbarHostState = snackbarHostState)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritePlaceFolderBottomSheetPreview() {
    TuripTheme {
        Surface(
            color = TuripTheme.colors.primarySub,
            shape = TuripTheme.shape.bottomSheetRounded,
        ) {
            FoldersContent(
                placeName = "장소명",
                enableConfirm = false,
                folders = persistentListOf(),
                onAddFolderClick = { },
                onFavoriteClick = { },
                onNavigateToFolder = { },
                onConfirmClick = { },
            )
        }
    }
}
