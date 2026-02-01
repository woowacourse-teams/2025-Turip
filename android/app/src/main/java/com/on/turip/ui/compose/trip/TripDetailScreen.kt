package com.on.turip.ui.compose.trip

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.on.turip.R
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.common.model.trip.TripDurationModel
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.component.TuripSnackbarVisuals
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.component.ContentBookmarkButton
import com.on.turip.ui.compose.trip.component.ContentInformation
import com.on.turip.ui.compose.trip.component.ContentVideo
import com.on.turip.ui.compose.trip.component.CreatorInformation
import com.on.turip.ui.compose.trip.component.Days
import com.on.turip.ui.compose.trip.component.PlaceItem
import com.on.turip.ui.compose.trip.component.TripDetailAppBar
import com.on.turip.ui.compose.trip.model.DayModel
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.trip.model.PlaceModel
import com.on.turip.ui.compose.trip.model.TripDetailInfoModel
import com.on.turip.ui.compose.trip.webview.VideoManager
import kotlinx.collections.immutable.persistentListOf

@Composable
fun TripDetailScreen(
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToMap: (mapModel: MapModel) -> Unit,
    navigateToWebViewUrl: (url: String) -> Unit,
    onTuripPlaceClick: (id: Long, placeName: String) -> Unit,
    viewModel: TripDetailViewModel = hiltViewModel(),
) {
    val uiState: TripDetailUiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val listState = rememberLazyListState()
    val isAtBottom by remember {
        derivedStateOf {
            val lastVisibleItemIndex =
                listState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index
            val totalItemsCount = listState.layoutInfo.totalItemsCount

            lastVisibleItemIndex == totalItemsCount - 1
        }
    }

    val webViewController =
        rememberTripDetailWebViewController(
            context = context,
            navigateToWebViewUrl = navigateToWebViewUrl,
        )

    val isInitialLoading by remember {
        derivedStateOf { uiState.isLoading || webViewController.isLoading }
    }

    HandleFullScreenWindowLaunchedEffect(webViewController.isFullScreen)

    LaunchedEffect(uiState.tripDetailInfo.videoLink) {
        webViewController.loadVideo(uiState.tripDetailInfo.videoLink)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: TripDetailUiEffect ->
            handleUiEffect(
                uiEffect = uiEffect,
                snackbarHostState = snackbarHostState,
                context = context,
                navigateToLogin = navigateToLogin,
                handleErrorRetryRequest = viewModel::handleErrorRetryRequest,
            )
        }
    }

    BackHandler {
        when {
            webViewController.canHandleBack() -> webViewController.handleBack()
            else -> navigateToBack()
        }
    }

    DisposableEffect(Unit) {
        onDispose { webViewController.clear() }
    }

    Scaffold(
        topBar = {
            if (!webViewController.isFullScreen) {
                Column {
                    Spacer(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .windowInsetsTopHeight(WindowInsets.systemBars)
                                .background(TuripTheme.colors.primary),
                    )
                    TripDetailAppBar(
                        isError = uiState.errorUiState != ErrorUiState.None,
                        isBookmarked = uiState.isBookmarked,
                        onBackClick = navigateToBack,
                        onBookmarkClick = {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            viewModel.updateBookmark()
                        },
                    )
                }
            }
        },
        snackbarHost = {
            val bottomPadding by animateDpAsState(
                targetValue = if (isAtBottom) 50.dp else 0.dp,
                label = "snackbarPadding",
            )
            TuripSnackbar(
                snackbarHostState = snackbarHostState,
                modifier = Modifier.padding(bottom = bottomPadding),
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
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
                    if (!webViewController.isFullScreen) {
                        TripDetailScreenContent(
                            uiState = uiState,
                            listState = listState,
                            webViewController = webViewController,
                            onDayClick = viewModel::updateDay,
                            onTimeLineClick = webViewController::seekTo,
                            onMapClick = navigateToMap,
                            onTuripPlaceClick = onTuripPlaceClick,
                            onBookmarkClick = {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                viewModel.updateBookmark()
                            },
                            onErrorVideoClick = { navigateToWebViewUrl(uiState.tripDetailInfo.videoLink) },
                        )
                    } else {
                        FullScreenVideo(webViewController.fullScreenVideo)
                    }
                }
            }
        }
    }
}

private suspend fun handleUiEffect(
    uiEffect: TripDetailUiEffect,
    snackbarHostState: SnackbarHostState,
    context: Context,
    navigateToLogin: () -> Unit,
    handleErrorRetryRequest: (action: TripDetailRetryAction) -> Unit,
) {
    when (uiEffect) {
        is TripDetailUiEffect.ShowBookmarkStatus -> {
            val messageResource: Int =
                if (uiEffect.isBookmarked) R.string.trip_detail_snackbar_bookmark_save else R.string.trip_detail_snackbar_bookmark_remove
            val iconResource: Int =
                if (uiEffect.isBookmarked) R.drawable.btn_bookmark_selected else R.drawable.btn_bookmark_normal
            snackbarHostState.showSnackbar(
                visuals =
                    TuripSnackbarVisuals(
                        message = context.getString(messageResource),
                        actionLabel = context.getString(R.string.all_close_description),
                        iconRes = iconResource,
                    ),
            )
        }

        TripDetailUiEffect.NavigateToLogin -> {
            navigateToLogin()
        }

        is TripDetailUiEffect.ShowError -> {
            val uiModel: ErrorUiModel =
                uiEffect.errorUiState.toUiModel() ?: return
            val result =
                snackbarHostState.showSnackbar(
                    message = context.getString(uiModel.titleRes),
                    actionLabel = context.getString(uiModel.retryTextRes),
                    duration = SnackbarDuration.Long,
                )
            if (result == SnackbarResult.ActionPerformed) {
                handleErrorRetryRequest(uiEffect.retryAction)
            }
        }
    }
}

@Composable
private fun TripDetailScreenContent(
    uiState: TripDetailUiState,
    listState: LazyListState,
    webViewController: TripDetailWebViewController,
    onDayClick: (day: Int) -> Unit,
    onTimeLineClick: (timeLine: Int) -> Unit,
    onMapClick: (mapModel: MapModel) -> Unit,
    onTuripPlaceClick: (id: Long, placeName: String) -> Unit,
    onBookmarkClick: () -> Unit,
    onErrorVideoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
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

        item {
            Days(
                days = uiState.days,
                onDayClick = onDayClick,
                modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge),
            )
        }

        item {
            Text(
                text = stringResource(R.string.trip_detail_day_place_count, uiState.places.size),
                style = TuripTheme.typography.info1,
                color = TuripTheme.colors.gray05,
                textAlign = TextAlign.End,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TuripTheme.spacing.extraLarge)
                        .padding(
                            top = TuripTheme.spacing.extraSmall,
                            bottom = TuripTheme.spacing.medium,
                        ),
            )
        }

        items(items = uiState.places, key = { it.id }) { place ->
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
                        )
                        .fillMaxWidth(),
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
                        )
                        .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun FullScreenVideo(fullScreenVideo: View?) {
    if (fullScreenVideo == null) return
    AndroidView(
        factory = { context ->
            FrameLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        },
        update = { view: FrameLayout ->
            if (view.getChildAt(0) == fullScreenVideo) return@AndroidView
            (fullScreenVideo.parent as? ViewGroup)?.removeView(fullScreenVideo)
            view.removeAllViews()
            view.addView(fullScreenVideo)
        },
        modifier = Modifier.fillMaxSize(),
        onRelease = { (fullScreenVideo.parent as? ViewGroup)?.removeView(fullScreenVideo) },
    )
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
        TuripTheme {
            TripDetailScreenContent(
                uiState =
                    TripDetailUiState(
                        isLoading = false,
                        errorUiState = ErrorUiState.None,
                        tripDetailInfo = tripDetailInfo,
                        days = persistentListOf(DayModel(1), DayModel(2)),
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
                    ),
                listState = rememberLazyListState(),
                webViewController =
                    TripDetailWebViewController(
                        webView = webView,
                        videoManager = VideoManager(webView),
                        navigateToWebViewUrl = {},
                    ),
                onDayClick = {},
                onTimeLineClick = {},
                onMapClick = {},
                onTuripPlaceClick = { _, _ -> },
                onBookmarkClick = {},
                onErrorVideoClick = {},
            )
        }
    }
}
