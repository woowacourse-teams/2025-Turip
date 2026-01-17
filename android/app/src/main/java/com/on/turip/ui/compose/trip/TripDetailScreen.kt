package com.on.turip.ui.compose.trip

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.on.turip.R
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.component.TuripSnackbarVisuals
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.component.ContentFavoriteButton
import com.on.turip.ui.compose.trip.component.ContentInformation
import com.on.turip.ui.compose.trip.component.CreatorInformation
import com.on.turip.ui.compose.trip.component.Days
import com.on.turip.ui.compose.trip.component.PlaceItem
import com.on.turip.ui.compose.trip.component.TripDetailAppBar
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.trip.webview.TuripWebChromeClient
import com.on.turip.ui.compose.trip.webview.TuripWebViewClient
import com.on.turip.ui.compose.trip.webview.VideoManager
import com.on.turip.ui.compose.trip.webview.applyVideoSettings
import com.on.turip.ui.compose.trip.webview.navigateToTimeLine
import com.on.turip.ui.trip.detail.TripDetailViewModel

@Composable
fun TripDetailScreen(
    navigateToBack: () -> Unit = {},
    navigateToLogin: () -> Unit = {},
    navigateToMap: (mapModel: MapModel) -> Unit = {},
    navigateToWebViewUrl: (url: String) -> Unit = {},
    onClickFavoritePlace: (id: Long) -> Unit = {},
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
    val webViewClient =
        remember {
            TuripWebViewClient(
                onLoadingStarted = webViewState::loadingStarted,
                onLoadingFinished = {
                    webViewState.loadingFinished()
                    webViewState.updateWebViewError(false)
                },
                onNavigateExternalUrl = navigateToWebViewUrl,
            )
        }
    val videoManager = remember { VideoManager(webView) }

    val activity = context as? Activity
    LaunchedEffect(webViewState.isFullScreen) {
        val window = activity?.window ?: return@LaunchedEffect
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        if (webViewState.isFullScreen) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            controller.show(WindowInsetsCompat.Type.systemBars())

//            controller.isAppearanceLightStatusBars = true
//            controller.isAppearanceLightNavigationBars = true
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }

    LaunchedEffect(uiState.tripDetailInfo.videoLink) {
        val url = uiState.tripDetailInfo.videoLink
        if (url.isNotEmpty() && url != webView.url) {
            videoManager.loadVideo(uiState.tripDetailInfo.videoLink) {
                webViewState.updateWebViewError(true)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: TripDetailUiEffect ->
            handleUiEffect(
                uiEffect = uiEffect,
                snackbarHostState = snackbarHostState,
                context = context,
                navigateToLogin = navigateToLogin,
                handleErrorRetryRequest = { viewModel.handleErrorRetryRequest(it) },
            )
        }
    }

    BackHandler {
        when {
            webViewState.isFullScreen -> webChromeClient.onHideCustomView()
            webView.canGoBack() -> webView.goBack()
            else -> navigateToBack()
        }
    }

    DisposableEffect(Unit) {
        webView.webChromeClient = webChromeClient
        webView.webViewClient = webViewClient

        onDispose {
            webView.destroy()
            videoManager.clear()
        }
    }

    Scaffold(
        topBar = {
            if (!webViewState.isFullScreen) {
                TripDetailAppBar(
                    isContentFavorite = uiState.isFavorite,
                    onBackClick = navigateToBack,
                    onContentFavoriteClick = viewModel::updateFavorite,
                )
            }
        },
        contentWindowInsets = WindowInsets(),
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
        modifier =
            if (!webViewState.isFullScreen) {
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            } else {
                Modifier
            },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            TripDetailScreenContent(
                uiState = uiState,
                webView = webView,
                webViewState = webViewState,
                onDayClick = { viewModel.updateDay(it) },
                onTimeLineClick = { webView.navigateToTimeLine(it) },
                onMapClick = { navigateToMap(it) },
                onFavoritePlaceClick = { onClickFavoritePlace(it) },
                onFavoriteContentClick = viewModel::updateFavorite,
            )

            if (webViewState.isFullScreen) {
                AndroidView(
                    factory = { context ->
                        FrameLayout(context).apply {
                            layoutParams =
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                )
                            addView(webViewState.fullScreenVideo)
                        }
                    },
                    update = { view ->
                        // 2. 비디오 뷰의 기존 부모가 있다면 안전하게 제거
                        (view.parent as? ViewGroup)?.removeView(webViewState.fullScreenVideo)

                        // 3. 현재 레이아웃의 기존 자식들을 모두 비우고 새로 추가
                        view.removeAllViews()
                        view.addView(webViewState.fullScreenVideo)
                    },
                    modifier = Modifier.fillMaxSize(),
                    onRelease = {
                        val videoView = webViewState.fullScreenVideo
                        (videoView?.parent as? ViewGroup)?.removeView(videoView)
                    },
                )
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
        is TripDetailUiEffect.ShowFavoriteStatus -> {
            val messageResource: Int =
                if (uiEffect.isFavorite) R.string.trip_detail_snackbar_favorite_save else R.string.trip_detail_snackbar_favorite_remove
            val iconResource: Int =
                if (uiEffect.isFavorite) R.drawable.ic_heart_pressed else R.drawable.ic_heart_empty
            snackbarHostState.showSnackbar(
                visuals =
                    TuripSnackbarVisuals(
                        message = context.getString(messageResource),
                        actionLabel = context.getString(R.string.all_snackbar_close),
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
    webView: WebView,
    webViewState: TripDetailWebViewState,
    onDayClick: (day: Int) -> Unit,
    onTimeLineClick: (timeLine: Int) -> Unit,
    onMapClick: (mapModel: MapModel) -> Unit,
    onFavoritePlaceClick: (id: Long) -> Unit,
    onFavoriteContentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
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
                webView = webView,
                webViewState = webViewState,
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            ContentInformation(information = uiState.tripDetailInfo)
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Days(
                days = uiState.days,
                onDayClick = { day -> onDayClick(day) },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(items = uiState.places, key = { it.id }) { place ->
            PlaceItem(
                placeModel = place,
                onTimeLineClick = { onTimeLineClick(it) },
                onMapClick = { onMapClick(it) },
                onFavoriteClick = { onFavoritePlaceClick(it) },
                modifier =
                    Modifier
                        .padding(start = 20.dp, end = 20.dp, bottom = 8.dp)
                        .fillMaxWidth(),
            )
        }

        item {
            ContentFavoriteButton(
                isContentFavorite = uiState.isFavorite,
                onClick = onFavoriteContentClick,
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ContentVideo(
    webView: WebView,
    webViewState: TripDetailWebViewState,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f),
    ) {
        AndroidView(
            factory = {
                webView.apply {
                    layoutParams =
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                    setBackgroundColor(0x0000000)
                }
            },
            update = { view ->
                view.visibility = if (webViewState.isFullScreen) View.INVISIBLE else View.VISIBLE
                if (!webViewState.isFullScreen) {
                    view.post {
                        view.requestLayout()
                        view.invalidate()
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
        if (webViewState.isLoading) {
            CircularProgressIndicator(
                modifier =
                    Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                color = TuripTheme.colors.primary,
            )
        }
        if (webViewState.isError) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(TuripTheme.colors.black),
            ) {
                Text(
                    text = "에러",
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripContentScreenPreview() {
    TuripTheme {
        TripDetailScreen(
            navigateToBack = {},
            navigateToLogin = {},
        )
    }
}
