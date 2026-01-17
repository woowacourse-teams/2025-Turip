package com.on.turip.ui.compose.trip

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.View
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.webview.TuripWebChromeClient
import com.on.turip.ui.compose.trip.webview.TuripWebViewClient
import com.on.turip.ui.compose.trip.webview.VideoManager
import com.on.turip.ui.compose.trip.webview.applyVideoSettings
import com.on.turip.ui.compose.trip.webview.navigateToTimeLine
import com.on.turip.ui.compose.trip.webview.resumeVideo

@Stable
class TripDetailWebViewController(
    val webView: WebView,
    private val videoManager: VideoManager,
    val navigateToWebViewUrl: (String) -> Unit,
) {
    var isFullScreen by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(true)
        private set
    var isError by mutableStateOf(false)
        private set
    var fullScreenVideo by mutableStateOf<View?>(null)
        private set

    private val webChromeClient =
        TuripWebChromeClient(
            onShowFullScreen = { video: View ->
                fullScreenVideo = video
                isFullScreen = true

                webView.resumeVideo()
            },
            onHideFullScreen = {
                fullScreenVideo = null
                isFullScreen = false

                webView.resumeVideo()
            },
        )

    private val webViewClient =
        TuripWebViewClient(
            onLoadingStarted = { isLoading = true },
            onLoadingFinished = { isLoading = false },
            onNavigateExternalUrl = navigateToWebViewUrl,
        )

    init {
        webView.webViewClient = webViewClient
        webView.webChromeClient = webChromeClient
    }

    fun loadVideo(url: String) {
        if (url.isNotEmpty() && url != webView.url) {
            videoManager.loadVideo(url) { isError = true }
        }
    }

    fun seekTo(seconds: Int) {
        webView.navigateToTimeLine(seconds)
    }

    fun canHandleBack(): Boolean = webView.canGoBack() || isFullScreen

    fun handleBack() {
        when {
            isFullScreen -> webChromeClient.onHideCustomView()
            webView.canGoBack() -> webView.goBack()
        }
    }

    fun clear() {
        videoManager.clear()
        webView.destroy()
    }
}

@Composable
fun rememberTripDetailWebViewController(
    context: Context,
    navigateToWebViewUrl: (String) -> Unit,
): TripDetailWebViewController =
    remember {
        val webView =
            WebView(context).apply { applyVideoSettings() }

        TripDetailWebViewController(
            webView = webView,
            videoManager = VideoManager(webView),
            navigateToWebViewUrl = navigateToWebViewUrl,
        )
    }

@Composable
fun HandleFullScreenWindowLaunchedEffect(isFullScreen: Boolean) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return
    val statusBarColor = TuripTheme.colors.primary.toArgb()

    LaunchedEffect(isFullScreen) {
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        if (isFullScreen) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            controller.show(WindowInsetsCompat.Type.systemBars())

            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false

            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }
}
