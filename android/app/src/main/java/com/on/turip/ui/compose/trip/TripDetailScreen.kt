package com.on.turip.ui.compose.trip

import android.content.res.Resources
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.common.model.trip.TripDurationModel
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.component.TuripSnackbarVisuals
import com.on.turip.ui.compose.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.ui.compose.designsystem.snackbar.SnackbarDelegate
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.main.component.LocalSystemBarStyleController
import com.on.turip.ui.compose.main.component.SystemBarStyle
import com.on.turip.ui.compose.trip.component.ContentBookmarkButton
import com.on.turip.ui.compose.trip.component.ContentInformation
import com.on.turip.ui.compose.trip.component.ContentVideo
import com.on.turip.ui.compose.trip.component.CreatorInformation
import com.on.turip.ui.compose.trip.component.PlaceItem
import com.on.turip.ui.compose.trip.component.TripDetailAppBar
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.trip.model.PlaceModel
import com.on.turip.ui.compose.trip.model.SelectedPlaceModel
import com.on.turip.ui.compose.trip.model.TripDetailInfoModel
import com.on.turip.ui.compose.trip.turipselection.PlaceTuripSelectionBottomSheet
import com.on.turip.ui.compose.trip.webview.VideoManager
import com.on.turip.ui.compose.turip.component.TuripAddBottomSheet
import com.on.turip.ui.compose.turipdetail.model.turip.TuripShareModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    contentId: Long,
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToTuripDetail: (turipId: Long) -> Unit,
    navigateToMap: (mapModel: MapModel) -> Unit,
    navigateToWebViewUrl: (url: String) -> Unit,
    navigateToShareTuripByText: (turipShareModel: TuripShareModel) -> Unit,
    navigateToShareTuripInvitationLink: (invitationLink: String) -> Unit,
    viewModel: TripDetailViewModel = hiltViewModel(),
) {
    val uiState: TripDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarDelegate = LocalSnackbarDelegate.current
    val systemBarStyleController = LocalSystemBarStyleController.current
    val context = LocalContext.current
    val resources = LocalResources.current

    val listState = rememberLazyListState()

    val webViewController =
        rememberTripDetailWebViewController(
            context = context,
            navigateToWebViewUrl = navigateToWebViewUrl,
        )

    val isInitialLoading by remember {
        derivedStateOf { uiState.isLoading || webViewController.isLoading }
    }
    val isAtBottom by remember(
        listState,
        isInitialLoading,
        uiState.errorUiState,
        webViewController.isFullScreen,
    ) {
        derivedStateOf {
            !isInitialLoading &&
                uiState.errorUiState == ErrorUiState.None &&
                !webViewController.isFullScreen &&
                !listState.canScrollForward
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedPlace by remember { mutableStateOf<SelectedPlaceModel?>(null) }
    val bottomSheetScope = rememberCoroutineScope()

    val addTuripSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addTuripSnackbarHostState = remember { SnackbarHostState() }
    val addTuripSheetSnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initContentId(contentId)
    }

    // bottomSheet visibility logic
    LaunchedEffect(uiState.selectedPlaceModel) {
        val currentSelectedPlace: SelectedPlaceModel? = uiState.selectedPlaceModel
        if (currentSelectedPlace != null) {
            selectedPlace = currentSelectedPlace
            sheetState.show()
        } else {
            // 바텀시트 데이터가 초기화되었는데 캐싱 데이터가 존재한다면
            if (selectedPlace != null) {
                if (sheetState.currentValue != SheetValue.Hidden) sheetState.hide()
                selectedPlace = null
            }
        }
    }

    HandleFullScreenWindowLaunchedEffect(isFullScreen = webViewController.isFullScreen)

    LaunchedEffect(uiState.tripDetailInfo.videoLink) {
        webViewController.loadVideo(uiState.tripDetailInfo.videoLink)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: TripDetailUiEffect ->
            handleUiEffect(
                uiEffect = uiEffect,
                snackbarDelegate = snackbarDelegate,
                resources = resources,
                navigateToLogin = navigateToLogin,
                handleErrorRetryRequest = viewModel::handleErrorRetryRequest,
                addTuripSheetState = addTuripSheetState,
                dismissAddTuripBottomSheet = viewModel::dismissAddTuripBottomSheet,
                snackbarState = addTuripSnackbarHostState,
                addTuripSheetSnackbarState = addTuripSheetSnackbarHostState,
            )
        }
    }
    LaunchedEffect(isAtBottom) {
        snackbarDelegate.updateBottomPadding(if (isAtBottom) 50.dp else 0.dp)
    }

    SideEffect {
        systemBarStyleController.update(
            if (webViewController.isFullScreen) {
                SystemBarStyle()
            } else {
                SystemBarStyle(
                    isLightStatusBarIcons = false,
                    isLightNavigationBarIcons = false,
                )
            },
        )
    }

    BackHandler {
        when {
            webViewController.canHandleBack() -> webViewController.handleBack()
            else -> navigateToBack()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            systemBarStyleController.reset()
            snackbarDelegate.updateBottomPadding(0.dp)
            webViewController.clear()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (!webViewController.isFullScreen) {
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsTopHeight(WindowInsets.systemBars)
                        .background(TuripTheme.colors.primary),
            )
            TripDetailAppBar(
                isError = uiState.errorUiState != ErrorUiState.None,
                isLoading = isInitialLoading,
                isBookmarked = uiState.isBookmarked,
                onBackClick = navigateToBack,
                onBookmarkClick = {
                    viewModel.updateBookmark()
                },
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.errorUiState != ErrorUiState.None -> {
                    ErrorScreen(
                        errorUiState = uiState.errorUiState,
                        onRetryClick = viewModel::loadTripDetails,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                isInitialLoading -> {
                    CircularProgressIndicator(
                        modifier =
                            Modifier
                                .size(60.dp)
                                .align(Alignment.Center),
                        color = TuripTheme.colors.primary,
                    )
                }

                else -> {
                    TripDetailScreenContent(
                        uiState = uiState,
                        listState = listState,
                        webViewController = webViewController,
                        onTimeLineClick = webViewController::seekTo,
                        onMapClick = navigateToMap,
                        onTuripPlaceClick = viewModel::selectPlace,
                        onBookmarkClick = {
                            viewModel.updateBookmark()
                        },
                        onErrorVideoClick = { navigateToWebViewUrl(uiState.tripDetailInfo.videoLink) },
                    )

                    if (!webViewController.isFullScreen) {
                        selectedPlace?.let { place ->
                            PlaceTuripSelectionBottomSheet(
                                sheetState = sheetState,
                                selectedPlaceModel = place,
                                snackbarHostState = addTuripSnackbarHostState,
                                onNavigateToLogin = navigateToLogin,
                                onNavigateToAddTurip = { viewModel.showAddTuripBottomSheet() },
                                onNavigateToTuripDetail = navigateToTuripDetail,
                                onNavigateToMap = navigateToMap,
                                onShareTuripByText = navigateToShareTuripByText,
                                onShareTuripInvitationLink = navigateToShareTuripInvitationLink,
                                onPlaceTuripChanged = viewModel::updatePlaceTuripSelection,
                                onDismiss = {
                                    bottomSheetScope.launch {
                                        sheetState.hide()
                                        viewModel.clearSelectedPlace()
                                        selectedPlace = null
                                    }
                                },
                            )
                        }

                        if (uiState.showAddTuripBottomSheet) {
                            TuripAddBottomSheet(
                                title = resources.getString(R.string.bottom_sheet_turip_add_title),
                                turipName = uiState.addTuripInputName,
                                sheetState = addTuripSheetState,
                                turipNameStatus = uiState.addTuripNameStatus,
                                isConfirmEnabled = uiState.addTuripNameStatus.isConfirmEnabled && !uiState.isCreatingTurip,
                                onNameChanged = viewModel::updateAddTuripInputName,
                                onConfirmClick = viewModel::addTurip,
                                onDismiss = viewModel::dismissAddTuripBottomSheet,
                                snackbarHostState = addTuripSheetSnackbarHostState,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private suspend fun handleUiEffect(
    uiEffect: TripDetailUiEffect,
    snackbarDelegate: SnackbarDelegate,
    snackbarState: SnackbarHostState,
    addTuripSheetSnackbarState: SnackbarHostState,
    resources: Resources,
    navigateToLogin: () -> Unit,
    handleErrorRetryRequest: (action: TripDetailRetryAction) -> Unit,
    addTuripSheetState: SheetState,
    dismissAddTuripBottomSheet: () -> Unit,
) {
    when (uiEffect) {
        is TripDetailUiEffect.ShowBookmarkStatus -> {
            val messageResource: Int =
                if (uiEffect.isBookmarked) R.string.trip_detail_snackbar_bookmark_save else R.string.trip_detail_snackbar_bookmark_remove
            val iconResource: Int =
                if (uiEffect.isBookmarked) R.drawable.btn_bookmark_selected else R.drawable.btn_bookmark_normal
            snackbarDelegate.showSnackbar(
                message = resources.getString(messageResource),
                actionLabel = resources.getString(R.string.all_close_description),
                iconRes = iconResource,
            )
        }

        TripDetailUiEffect.NavigateToLogin -> {
            navigateToLogin()
        }

        is TripDetailUiEffect.ShowUpdatedTuripSelectionByPlace -> {
            val messageResource: Int = R.string.trip_detail_turip_selection_updated
            val iconResource: Int = R.drawable.btn_turip_selected
            snackbarDelegate.showSnackbar(
                message = resources.getString(messageResource, uiEffect.placeName),
                actionLabel = resources.getString(R.string.all_close_description),
                iconRes = iconResource,
            )
        }

        is TripDetailUiEffect.ShowError -> {
            val uiModel: ErrorUiModel =
                uiEffect.errorUiState.toUiModel() ?: return
            when (uiEffect.retryAction) {
                is TripDetailRetryAction.AddTurip -> {
                    val result =
                        addTuripSheetSnackbarState.showSnackbar(
                            visuals =
                                TuripSnackbarVisuals(
                                    message = resources.getString(uiModel.titleRes),
                                    actionLabel = resources.getString(uiModel.retryTextRes),
                                    duration = SnackbarDuration.Long,
                                ),
                        )
                    if (result == SnackbarResult.ActionPerformed) {
                        handleErrorRetryRequest(uiEffect.retryAction)
                    }
                }

                else -> {
                    snackbarDelegate.showSnackbar(
                        message = resources.getString(uiModel.titleRes),
                        actionLabel = resources.getString(uiModel.retryTextRes),
                        duration = SnackbarDuration.Long,
                        onAction = { handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }
            }
        }

        is TripDetailUiEffect.TuripAdded -> {
            addTuripSheetState.hide()
            dismissAddTuripBottomSheet()
            snackbarState.showSnackbar(
                visuals =
                    TuripSnackbarVisuals(
                        message =
                            resources.getString(
                                R.string.turip_added_snackbar_message,
                                uiEffect.turipName,
                            ),
                        iconRes = R.drawable.btn_turip_selected,
                        actionLabel = resources.getString(R.string.all_close_description),
                    ),
            )
        }
    }
}

@Composable
private fun TripDetailScreenContent(
    uiState: TripDetailUiState,
    listState: LazyListState,
    webViewController: TripDetailWebViewController,
    onTimeLineClick: (timeLine: Int) -> Unit,
    onMapClick: (mapModel: MapModel) -> Unit,
    onTuripPlaceClick: (id: Long, placeName: String) -> Unit,
    onBookmarkClick: () -> Unit,
    onErrorVideoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize().navigationBarsPadding(),
    ) {
        item {
            CreatorInformation(
                thumbnailUrl = uiState.tripDetailInfo.creatorThumbnail,
                name = uiState.tripDetailInfo.creatorName,
            )
        }

        stickyHeader {
            ContentVideo(
                onErrorClick = onErrorVideoClick,
                webViewController = webViewController,
            )
        }

        item {
            Spacer(modifier = Modifier.height(TuripTheme.spacing.extraLarge))
            ContentInformation(information = uiState.tripDetailInfo)
            Spacer(modifier = Modifier.height(TuripTheme.spacing.large))
        }

        items(
            items = uiState.places,
            key = { "${it.id}_${it.timeLine}" },
        ) { place ->
            PlaceItem(
                placeModel = place,
                onTimeLineClick = onTimeLineClick,
                onMapClick = onMapClick,
                onTuripPlaceClick = onTuripPlaceClick,
                modifier =
                    Modifier
                        .padding(
                            start = TuripTheme.spacing.extraLarge,
                            end = TuripTheme.spacing.extraLarge,
                            bottom = TuripTheme.spacing.small,
                        ).fillMaxWidth(),
            )
        }

        item {
            ContentBookmarkButton(
                isBookmarked = uiState.isBookmarked,
                onClick = onBookmarkClick,
                modifier =
                    Modifier
                        .padding(
                            horizontal = TuripTheme.spacing.extraLarge,
                            vertical = TuripTheme.spacing.medium,
                        ).fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripContentScreenPreview() {
    val tripDetailInfo =
        TripDetailInfoModel(
            creatorName = "여행하는 튜립팀",
            creatorThumbnail = "",
            city = "서울",
            videoLink = "",
            contentTitle = "겨울엔 어떤 나라를 여행가면 좋을지 궁금하다 여행가고 싶다 여행 가고 싶 다",
            uploadedDate = "2026-01-18",
            placeTotalCount = 20,
            duration = TripDurationModel(1, 2),
        )
    val webView = WebView(LocalContext.current)
    TuripTheme {
        TripDetailScreenContent(
            uiState =
                TripDetailUiState(
                    isLoading = false,
                    errorUiState = ErrorUiState.None,
                    tripDetailInfo = tripDetailInfo,
                    places =
                        persistentListOf(
                            PlaceModel(
                                id = 1L,
                                name = "우아한테크코스",
                                isTuripPlace = true,
                                category = "💻 코딩맛집",
                                mapLink = "kakao.com/123123",
                                timeLine = "01:03",
                            ),
                            PlaceModel(
                                id = 2L,
                                name = "우아한테크코스",
                                isTuripPlace = false,
                                category = "💻 코딩맛집",
                                mapLink = "google.com/123123",
                                timeLine = "03:03",
                            ),
                        ),
                    isBookmarked = true,
                    selectedPlaceModel = null,
                ),
            listState = rememberLazyListState(),
            webViewController =
                TripDetailWebViewController(
                    webView = webView,
                    videoManager = VideoManager(webView),
                    navigateToWebViewUrl = {},
                    activity = LocalActivity.current!!,
                ),
            onTimeLineClick = {},
            onMapClick = {},
            onTuripPlaceClick = { _, _ -> },
            onBookmarkClick = {},
            onErrorVideoClick = {},
            modifier = Modifier.background(color = TuripTheme.colors.white),
        )
    }
}
