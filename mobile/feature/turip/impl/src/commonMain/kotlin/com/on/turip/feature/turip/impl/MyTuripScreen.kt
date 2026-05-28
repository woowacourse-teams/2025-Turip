package com.on.turip.feature.turip.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.on.turip.core.designsystem.component.TuripDialog
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.turip.impl.component.MyTuripCard
import com.on.turip.feature.turip.impl.component.MyTuripTabRow
import com.on.turip.feature.turip.impl.component.TuripAddBottomSheet
import com.on.turip.feature.turip.impl.viewmodel.MyTuripDialogState
import com.on.turip.feature.turip.impl.viewmodel.MyTuripEffect
import com.on.turip.feature.turip.impl.viewmodel.MyTuripIntent
import com.on.turip.feature.turip.impl.viewmodel.MyTuripViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MyTuripScreen(
    onNavigateToTuripDetail: (turipId: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: MyTuripViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current
    var isDeleteMode by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MyTuripEffect.NavigateToLogin -> onNavigateToLogin()
                is MyTuripEffect.ShowTuripAdded ->
                    snackbarDelegate.showSnackbar(message = "'${effect.name}' 튜립이 추가됐어요")
                is MyTuripEffect.ShowTuripDeleted ->
                    snackbarDelegate.showSnackbar(message = "'${effect.name}' 튜립이 삭제됐어요")
                MyTuripEffect.ShowLoadError ->
                    snackbarDelegate.showSnackbar(message = "튜립 목록을 불러오지 못했어요")
                MyTuripEffect.ShowActionError ->
                    snackbarDelegate.showSnackbar(message = "작업에 실패했어요. 다시 시도해주세요")
            }
        }
    }

    uiState.dialogState?.let { dialog ->
        when (dialog) {
            is MyTuripDialogState.RemoveTurip -> TuripDialog(
                title = "튜립 삭제",
                message = "'${dialog.turip.name}' 튜립을 삭제하시겠어요?",
                confirmText = "삭제",
                dismissText = "취소",
                confirmButtonColor = TuripTheme.colors.error,
                dismissButtonColor = TuripTheme.colors.gray02,
                onConfirmation = { viewModel.onIntent(MyTuripIntent.ConfirmDelete(dialog.turip)) },
                onDismissRequest = { viewModel.onIntent(MyTuripIntent.DismissDeleteDialog) },
            )
        }
    }

    if (uiState.showAddBottomSheet) {
        TuripAddBottomSheet(
            turipName = uiState.inputTuripName,
            isConfirmEnabled = uiState.inputTuripName.isNotBlank() && !uiState.isCreatingTurip,
            onNameChanged = { viewModel.onIntent(MyTuripIntent.UpdateTuripName(it)) },
            onConfirmClick = { viewModel.onIntent(MyTuripIntent.ConfirmAddTurip) },
            onDismiss = { viewModel.onIntent(MyTuripIntent.DismissAddBottomSheet) },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = TuripTheme.spacing.extraLarge),
        ) {
            Text(
                text = "나의 튜립",
                style = TuripTheme.typography.display,
                color = TuripTheme.colors.black,
                modifier = Modifier.padding(
                    top = TuripTheme.spacing.extraExtraLarge,
                    bottom = TuripTheme.spacing.large,
                ),
            )

            MyTuripTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.onIntent(MyTuripIntent.SelectTab(it)) },
            )

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = TuripTheme.spacing.large),
                ) {
                    items(items = uiState.filteredTurips, key = { it.id }) { turip ->
                        MyTuripCard(
                            turip = turip,
                            isDeleteMode = isDeleteMode,
                            onTuripClick = { id ->
                                if (isDeleteMode) {
                                    if (!turip.isDefault) {
                                        viewModel.onIntent(MyTuripIntent.ShowDeleteDialog(turip))
                                    }
                                } else {
                                    onNavigateToTuripDetail(id)
                                }
                            },
                            onLongPress = { isDeleteMode = true },
                            onDeleteClick = { viewModel.onIntent(MyTuripIntent.ShowDeleteDialog(turip)) },
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.onIntent(MyTuripIntent.ShowAddBottomSheet) },
            shape = CircleShape,
            containerColor = TuripTheme.colors.primary,
            contentColor = TuripTheme.colors.white,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(TuripTheme.spacing.extraLarge),
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }
}
