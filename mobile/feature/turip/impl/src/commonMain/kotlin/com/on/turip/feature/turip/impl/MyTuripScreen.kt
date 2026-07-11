package com.on.turip.feature.turip.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.on.turip.core.designsystem.component.TuripDialog
import com.on.turip.core.designsystem.component.TuripSnackbarVisuals
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_close_description
import com.on.turip.core.designsystem.generated.resources.all_mascot_description
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_add_title
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_delete
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_remove_approve
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_remove_cancel
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_remove_title
import com.on.turip.core.designsystem.generated.resources.btn_turip_selected
import com.on.turip.core.designsystem.generated.resources.mascot
import com.on.turip.core.designsystem.generated.resources.my_turip_screen_title
import com.on.turip.core.designsystem.generated.resources.my_turip_together_turip_empty_description
import com.on.turip.core.designsystem.generated.resources.my_turip_together_turip_empty_title
import com.on.turip.core.designsystem.generated.resources.turip_add_turip
import com.on.turip.core.designsystem.generated.resources.turip_delete_turip
import com.on.turip.core.designsystem.model.SnackbarIconModel
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.turip.TuripType
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.turip.impl.component.MyTuripCard
import com.on.turip.feature.turip.impl.component.MyTuripTabRow
import com.on.turip.feature.turip.impl.component.TuripAddBottomSheet
import com.on.turip.feature.turip.impl.model.MyTuripModel
import com.on.turip.feature.turip.impl.model.MyTuripTab
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTuripScreen(
    onNavigateToTuripDetail: (turipId: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyTuripViewModel = koinViewModel(),
) {
    val uiState: MyTuripUiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bottomSheetSnackbarHostState = remember { SnackbarHostState() }
    val turipSelectedPainter = painterResource(Res.drawable.btn_turip_selected)

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: MyTuripUiEffect ->
            when (uiEffect) {
                MyTuripUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is MyTuripUiEffect.ShowError -> {
                    val uiModel = uiEffect.errorUiModel ?: return@collect

                    snackbarDelegate.showSnackbar(
                        message = getString(uiModel.titleRes),
                        actionLabel = getString(uiModel.retryTextRes),
                        duration = SnackbarDuration.Long,
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                is MyTuripUiEffect.TuripAdded -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.turip_add_turip).formatResource(uiEffect.turipName),
                        icon = SnackbarIconModel.PainterIcon(turipSelectedPainter),
                        actionLabel = getString(Res.string.all_close_description),
                    )
                }

                is MyTuripUiEffect.TuripDeleted -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.turip_delete_turip).formatResource(uiEffect.turipName),
                        actionLabel = getString(Res.string.all_close_description),
                    )
                }

                is MyTuripUiEffect.ShowBottomSheetError -> {
                    val uiModel = uiEffect.errorUiModel ?: return@collect
                    val result =
                        bottomSheetSnackbarHostState.showSnackbar(
                            visuals =
                                TuripSnackbarVisuals(
                                    message = getString(uiModel.titleRes),
                                    actionLabel = getString(uiModel.retryTextRes),
                                    duration = SnackbarDuration.Long,
                                ),
                        )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.handleErrorRetryRequest(uiEffect.retryAction)
                    }
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
                title = stringResource(Res.string.bottom_sheet_turip_add_title),
                turipName = uiState.inputTuripName,
                sheetState = sheetState,
                turipNameStatus = uiState.turipNameStatus,
                isConfirmEnabled = uiState.turipNameStatus.isConfirmEnabled && !uiState.isCreatingTurip,
                onNameChanged = viewModel::updateInputName,
                onConfirmClick = viewModel::addTurip,
                snackbarHostState = bottomSheetSnackbarHostState,
                onDismiss = viewModel::dismissAddBottomSheet,
            )
        }

        uiState.dialogState?.let { myTuripDialogState: MyTuripUiState.MyTuripDialogState ->
            when (myTuripDialogState) {
                is MyTuripUiState.MyTuripDialogState.RemoveTurip -> {
                    TuripDialog(
                        title = stringResource(Res.string.bottom_sheet_turip_delete),
                        message =
                            stringResource(Res.string.bottom_sheet_turip_remove_title)
                                .formatResource(myTuripDialogState.turip.name),
                        confirmText = stringResource(Res.string.bottom_sheet_turip_remove_approve),
                        dismissText = stringResource(Res.string.bottom_sheet_turip_remove_cancel),
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
                MyTuripTab.SOLO -> turips.filter { it.type == TuripType.SOLO }
                MyTuripTab.TOGETHER -> turips.filter { it.type == TuripType.TOGETHER }
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

    NavigationBackHandler(
        state = rememberNavigationEventState(currentInfo = NavigationEventInfo.None),
        isBackEnabled = isDeleteMode,
        onBackCompleted = { isDeleteMode = false },
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = TuripTheme.spacing.extraLarge)
                    .pointerInput(isDeleteMode) {
                        detectTapGestures {
                            if (isDeleteMode) isDeleteMode = false
                        }
                    },
        ) {
            Text(
                text = stringResource(Res.string.my_turip_screen_title),
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
                    items(items = filteredTurips, key = { it.id }) { turip ->
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

                if (selectedTab == MyTuripTab.TOGETHER && filteredTurips.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Spacer(modifier = Modifier.height(TuripTheme.spacing.huge))

                        Image(
                            painter = painterResource(Res.drawable.mascot),
                            contentDescription = stringResource(Res.string.all_mascot_description),
                            modifier = Modifier.size(90.dp),
                        )

                        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

                        Text(
                            text = stringResource(Res.string.my_turip_together_turip_empty_title),
                            style = TuripTheme.typography.title1,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

                        Text(
                            text = stringResource(Res.string.my_turip_together_turip_empty_description),
                            style = TuripTheme.typography.title2,
                            textAlign = TextAlign.Center,
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
                TuripType.TOGETHER,
                memberCount = 3,
                placeCount = 2,
                isDefault = true,
            ),
            MyTuripModel(1L, "수원 여행 계획 튜립", TuripType.SOLO, placeCount = 1, isDefault = false),
            MyTuripModel(2L, "수원 여행 계획 튜립", TuripType.SOLO, placeCount = 3, isDefault = false),
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
