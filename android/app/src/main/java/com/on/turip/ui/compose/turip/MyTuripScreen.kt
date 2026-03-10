package com.on.turip.ui.compose.turip

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.on.turip.R
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.turip.component.MyTuripCard
import com.on.turip.ui.compose.turip.component.MyTuripTabRow
import com.on.turip.ui.compose.turip.component.TuripAddBottomSheet
import com.on.turip.ui.compose.turip.model.MyTuripModel
import com.on.turip.ui.compose.turip.model.MyTuripTab
import com.on.turip.ui.compose.turip.model.TuripTypeModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTuripScreen(
    onNavigateToTuripDetail: (turipId: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyTuripViewModel = hiltViewModel(),
) {
    val uiState: MyTuripUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val resources = LocalResources.current
    val snackbarDelegate = LocalSnackbarDelegate.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadTurips()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: MyTuripUiEffect ->
            when (uiEffect) {
                MyTuripUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is MyTuripUiEffect.ShowError -> {
                    val uiModel = uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarDelegate.showSnackbar(
                        message = resources.getString(uiModel.titleRes),
                        actionLabel = resources.getString(uiModel.retryTextRes),
                        duration = SnackbarDuration.Long,
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                is MyTuripUiEffect.TuripAdded -> {
                    snackbarDelegate.showSnackbar(
                        message =
                            resources.getString(
                                R.string.turip_add_turip,
                                uiEffect.turipName,
                            ),
                        iconRes = R.drawable.btn_turip_selected,
                        actionLabel = resources.getString(R.string.all_close_description),
                    )
                }

                is MyTuripUiEffect.TuripDeleted -> {
                    snackbarDelegate.showSnackbar(
                        message =
                            resources.getString(
                                R.string.turip_delete_turip,
                                uiEffect.turipName,
                            ),
                        actionLabel = resources.getString(R.string.all_close_description),
                    )
                }

                is MyTuripUiEffect.ShowTuripRemoveFailed -> {
                    snackbarDelegate.showSnackbar(
                        message = resources.getString(R.string.retry_later),
                        actionLabel = resources.getString(R.string.all_close_description),
                    )
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MyTuripScreenContent(
            turips = uiState.turips,
            isLoading = uiState.isLoading && uiState.turips.isEmpty(),
            onTuripClick = onNavigateToTuripDetail,
            onTuripDelete = { myTuripModel: MyTuripModel ->
                viewModel.showTuripRemoveDialog(myTuripModel)
            },
            onAddClick = {
                viewModel.showAddBottomSheet()
            },
        )

        if (uiState.showAddBottomSheet) {
            TuripAddBottomSheet(
                title = stringResource(R.string.bottom_sheet_turip_add_title),
                turipName = uiState.inputTuripName,
                sheetState = sheetState,
                turipNameStatus = uiState.turipNameStatus,
                isConfirmEnabled = uiState.turipNameStatus.isConfirmEnabled && !uiState.isCreatingTurip,
                onNameChanged = viewModel::updateInputName,
                onConfirmClick = viewModel::addTurip,
                onDismiss = viewModel::dismissAddBottomSheet,
            )
        }

        uiState.dialogState?.let { myTuripDialogState: MyTuripUiState.MyTuripDialogState ->
            when (myTuripDialogState) {
                is MyTuripUiState.MyTuripDialogState.RemoveTurip -> {
                    TuripDialog(
                        title = stringResource(R.string.bottom_sheet_turip_delete),
                        message =
                            stringResource(
                                R.string.bottom_sheet_turip_remove_title,
                                myTuripDialogState.turip.name,
                            ),
                        confirmText = stringResource(R.string.bottom_sheet_turip_remove_approve),
                        dismissText = stringResource(R.string.bottom_sheet_turip_remove_cancel),
                        confirmButtonColor = TuripTheme.colors.error,
                        dismissButtonColor = TuripTheme.colors.gray02,
                        onConfirmation = {
                            viewModel.dismissTuripRemoveDialog()
                            viewModel.deleteTurip(myTuripDialogState.turip.id)
                        },
                        onDismissRequest = viewModel::dismissTuripRemoveDialog,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun MyTuripScreenContent(
    turips: ImmutableList<MyTuripModel>,
    isLoading: Boolean,
    onTuripClick: (turipId: Long) -> Unit,
    onTuripDelete: (myTuripModel: MyTuripModel) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab: MyTuripTab by rememberSaveable { mutableStateOf(MyTuripTab.ALL) }
    var isDeleteMode: Boolean by rememberSaveable { mutableStateOf(false) }

    val filteredTurips by remember(turips, selectedTab) {
        derivedStateOf {
            when (selectedTab) {
                MyTuripTab.ALL -> turips
                MyTuripTab.SOLO -> turips.filter { it.type == TuripTypeModel.SOLO }
                MyTuripTab.TOGETHER -> turips.filter { it.type == TuripTypeModel.TOGETHER }
            }
        }
    }
    val userTuripCount by remember(turips) {
        derivedStateOf { turips.count { !it.isDefault } }
    }

    LaunchedEffect(userTuripCount, isDeleteMode) {
        if (isDeleteMode && userTuripCount == 0) {
            isDeleteMode = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = TuripTheme.spacing.extraLarge)
                    .pointerInput(isDeleteMode) {
                        detectTapGestures {
                            if (isDeleteMode) isDeleteMode = false
                        }
                    },
        ) {
            Text(
                text = stringResource(R.string.my_turip_screen_title),
                style = TuripTheme.typography.display,
                color = TuripTheme.colors.black,
                modifier =
                    Modifier.padding(
                        top = TuripTheme.spacing.extraExtraLarge,
                        bottom = TuripTheme.spacing.large,
                    ),
            )

            MyTuripTabRow(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                    isDeleteMode = false
                },
            )

            if (!isLoading) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = TuripTheme.spacing.large),
                ) {
                    items(items = filteredTurips, key = { it.id }) { turip: MyTuripModel ->
                        MyTuripCard(
                            turipImage = turip.image,
                            turip = turip,
                            isDeleteMode = isDeleteMode,
                            onTuripClick = { id ->
                                if (isDeleteMode) {
                                    onTuripDelete(turip)
                                } else {
                                    onTuripClick(id)
                                }
                            },
                            onLongPress = { isDeleteMode = true },
                            onDeleteClick = { onTuripDelete(turip) },
                            isDefaultFolder = turip.isDefault,
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = TuripTheme.colors.primary,
            contentColor = TuripTheme.colors.white,
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(TuripTheme.spacing.extraLarge),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTuripScreenPreview() {
    val allTurips: ImmutableList<MyTuripModel> =
        persistentListOf(
            MyTuripModel(
                0L,
                "수원 여행 계획 튜립",
                TuripTypeModel.TOGETHER,
                memberCount = 3,
                placeCount = 2,
                isDefault = true,
            ),
            MyTuripModel(1L, "수원 여행 계획 튜립", TuripTypeModel.SOLO, placeCount = 1, isDefault = false),
            MyTuripModel(2L, "수원 여행 계획 튜립", TuripTypeModel.SOLO, placeCount = 3, isDefault = false),
        )

    TuripTheme {
        MyTuripScreenContent(
            turips = allTurips,
            isLoading = false,
            onTuripClick = {},
            onTuripDelete = {},
            onAddClick = {},
        )
    }
}
