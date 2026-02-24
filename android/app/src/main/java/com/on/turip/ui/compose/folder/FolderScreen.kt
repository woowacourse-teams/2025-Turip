package com.on.turip.ui.compose.myturip

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.on.turip.ui.common.extensions.showSnackbarWithAction
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.component.TuripSnackbarVisuals
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.folder.FolderUiEffect
import com.on.turip.ui.compose.folder.FolderUiState
import com.on.turip.ui.compose.folder.FolderViewModel
import com.on.turip.ui.compose.folder.component.FolderAddBottomSheet
import com.on.turip.ui.compose.folder.component.MyTuripCard
import com.on.turip.ui.compose.folder.component.MyTuripModel
import com.on.turip.ui.compose.folder.component.MyTuripTabRow
import com.on.turip.ui.compose.folder.component.TuripType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class MyTuripTab(
    val tabName: String,
) {
    ALL("전체"),
    SOLO("나홀로 튜립"),
    TOGETHER("함께 튜립"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTuripScreen(
    onNavigateToTuripPlace: (Long, String, Boolean) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FolderViewModel = hiltViewModel(),
) {
    val uiState: FolderUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var dialogState: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadTuripFolders()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: FolderUiEffect ->
            when (uiEffect) {
                FolderUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is FolderUiEffect.ShowError -> {
                    val uiModel = uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarHostState.showSnackbarWithAction(
                        message = resources.getString(uiModel.titleRes),
                        actionLabel = resources.getString(uiModel.retryTextRes),
                        duration = SnackbarDuration.Long,
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                is FolderUiEffect.TuripAdded -> {
                    snackbarHostState.showSnackbar(
                        visuals =
                            TuripSnackbarVisuals(
                                message =
                                    resources.getString(
                                        R.string.bottom_sheet_turip_add_turip,
                                        uiEffect.turipName,
                                    ),
                                actionLabel = resources.getString(R.string.all_close_description),
                            ),
                    )
                }

                is FolderUiEffect.TuripDeleted -> {
                    snackbarHostState.showSnackbar(
                        visuals =
                            TuripSnackbarVisuals(
                                message =
                                    resources.getString(
                                        R.string.bottom_sheet_turip_delete_turip,
                                        uiEffect.turipName,
                                    ),
                                actionLabel = resources.getString(R.string.all_close_description),
                            ),
                    )
                }
            }
        }
    }

    MyTuripScreenContent(
        turips = uiState.turips,
        snackbarHostState = snackbarHostState,
        onTuripClick = onNavigateToTuripPlace,
        onTuripDelete = { id: Long ->
            viewModel.updateDeleteTuripId(id)
            dialogState = true
        },
        onAddClick = viewModel::showAddBottomSheet,
        modifier = modifier,
    )

    if (uiState.showAddBottomSheet) {
        FolderAddBottomSheet(
            title = stringResource(R.string.bottom_sheet_turip_add_title),
            sheetState = sheetState,
            turipNameStatus = uiState.turipNameStatus,
            onNameChanged = viewModel::updateTuripName,
            onConfirmClick = viewModel::addTurip,
            onDismiss = viewModel::dismissAddBottomSheet,
        )
    }

    if (dialogState) {
        val deletedTuripName: String? =
            uiState.turips.find { it.id == uiState.deletedTuripId }?.name
        TuripDialog(
            title = "폴더 삭제",
            message = "정말 ${deletedTuripName}폴더를 삭제 하시겠습니까?",
            confirmText = "삭제",
            dismissText = "취소",
            confirmButtonColor = TuripTheme.colors.error,
            dismissButtonColor = TuripTheme.colors.gray02,
            onConfirmation = {
                viewModel.deleteTurip(uiState.deletedTuripId)
                dialogState = false
            },
            onDismissRequest = { dialogState = false },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun MyTuripScreenContent(
    turips: ImmutableList<MyTuripModel>,
    snackbarHostState: SnackbarHostState,
    onTuripClick: (Long, String, Boolean) -> Unit,
    onTuripDelete: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab: MyTuripTab by rememberSaveable { mutableStateOf(MyTuripTab.ALL) }
    var isDeleteMode: Boolean by rememberSaveable { mutableStateOf(false) }

    val filteredTurips: List<MyTuripModel> =
        when (selectedTab) {
            MyTuripTab.ALL -> turips
            MyTuripTab.SOLO -> turips.filter { it.type == TuripType.SOLO }
            MyTuripTab.TOGETHER -> turips.filter { it.type == TuripType.TOGETHER }
        }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = TuripTheme.colors.white,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
                    shape = CircleShape,
                    containerColor = TuripTheme.colors.primary,
                    contentColor = TuripTheme.colors.white,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                    )
                }
            },
        ) { innerPadding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = TuripTheme.spacing.extraLarge)
                        .pointerInput(isDeleteMode) {
                            detectTapGestures {
                                if (isDeleteMode) isDeleteMode = false
                            }
                        },
            ) {
                Text(
                    text = "내 튜립",
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

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = TuripTheme.spacing.large),
                ) {
                    items(items = filteredTurips, key = { it.id }) { turip: MyTuripModel ->
                        MyTuripCard(
                            turip = turip,
                            isDeleteMode = isDeleteMode,
                            onTuripClick = { id ->
                                if (isDeleteMode) {
                                    onTuripDelete(turip.id)
                                    if (filteredTurips.size == 1) isDeleteMode = false
                                } else {
                                    onTuripClick(id, turip.name, turip.isDefault)
                                }
                            },
                            onLongPress = { isDeleteMode = true },
                            onDeleteClick = {
                                onTuripDelete(turip.id)
                                if (filteredTurips.size == 1) isDeleteMode = false
                            },
                            isDefaultFolder = turip.isDefault,
                        )
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = TuripTheme.spacing.extraLarge),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTuripScreenPreview() {
    val snackbarHostState = remember { SnackbarHostState() }

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
        MyTuripScreenContent(allTurips, snackbarHostState, { _, _, _ -> }, {}, {})
    }
}
