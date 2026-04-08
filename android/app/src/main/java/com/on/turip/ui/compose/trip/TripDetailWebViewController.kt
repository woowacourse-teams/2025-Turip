package com.on.turip.ui.compose.trip

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
    val navigateToWebViewUrl: (webUrl: String) -> Unit,
    private val activity: Activity,
) {
    var isFullScreen by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(true)
        private set
    var isError by mutableStateOf(false)
        private set

    private var fullscreenContainer: FrameLayout? = null

    private val webChromeClient =
        TuripWebChromeClient(
            onShowFullScreen = { video: View ->
                // Compose state 변경 없이 decorView에 직접 추가 → config change 무관하게 안정적
                val container =
                    FrameLayout(activity).apply {
                        layoutParams =
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                            )
                    }
                container.addView(
                    video,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    ),
                )
                (activity.window.decorView as ViewGroup).addView(container)
                fullscreenContainer = container

                isFullScreen = true
                webView.resumeTimers()
                webView.resumeVideo()
            },
            onHideFullScreen = {
                fullscreenContainer?.let { container ->
                    container.removeAllViews()
                    (activity.window.decorView as ViewGroup).removeView(container)
                }
                fullscreenContainer = null
                isFullScreen = false

                webView.onResume()
                webView.resumeTimers()
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

    fun loadVideo(
        url: String,
        initialSecond: Int,
        onTimeUpdate: (time: Int) -> Unit,
    ) {
        if (url.isNotEmpty() && url != webView.url) {
            if (isError) isError = false
            videoManager.loadVideo(
                url = url,
                initialSecond = initialSecond,
                onTimeUpdate = onTimeUpdate,
                onError = { isError = true },
            )
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
        fullscreenContainer?.let { container ->
            container.removeAllViews()
            (activity.window.decorView as ViewGroup).removeView(container)
        }
        fullscreenContainer = null
        videoManager.clear()
        webView.destroy()
    }
}

@Composable
fun rememberTripDetailWebViewController(
    context: Context,
    navigateToWebViewUrl: (webUrl: String) -> Unit,
): TripDetailWebViewController {
    val activity = LocalActivity.current as? Activity
    return remember {
        val webView = WebView(context).apply { applyVideoSettings() }
        TripDetailWebViewController(
            webView = webView,
            videoManager = VideoManager(webView),
            navigateToWebViewUrl = navigateToWebViewUrl,
            activity = requireNotNull(activity) { "TripDetailWebViewController requires an Activity" },
        )
    }
}

@Composable
fun HandleFullScreenWindowLaunchedEffect(isFullScreen: Boolean) {
    val activity = LocalActivity.current ?: return
    val defaultOrientation =
        if (activity.resources.configuration.smallestScreenWidthDp < 600) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

    DisposableEffect(activity) {
        onDispose {
            val window = activity.window
            val controller = WindowCompat.getInsetsController(window, window.decorView)

            activity.requestedOrientation = defaultOrientation
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }

    LaunchedEffect(isFullScreen) {
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        if (isFullScreen) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            activity.requestedOrientation = defaultOrientation
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }
}
