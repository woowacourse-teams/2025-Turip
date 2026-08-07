package com.on.turip.feature.trip.impl

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Stable
actual class TripDetailWebViewController internal constructor(
    val webView: WebView,
    private val videoManager: VideoManager,
    val navigateToWebViewUrl: (webUrl: String) -> Unit,
    private val activity: Activity,
) {
    private var fullScreenState by mutableStateOf(false)
    actual val isFullScreen: Boolean get() = fullScreenState
    private var loadingState by mutableStateOf(true)
    actual val isLoading: Boolean get() = loadingState
    private var errorState by mutableStateOf(false)
    actual val isError: Boolean get() = errorState
    private var playbackRestoringState by mutableStateOf(false)
    actual val isPlaybackRestoring: Boolean get() = playbackRestoringState

    private var fullscreenContainer: FrameLayout? = null

    private val webChromeClient =
        TuripWebChromeClient(
            onShowFullScreen = { video: View ->
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

                fullScreenState = true
                webView.resumeTimers()
                webView.resumeVideo()
            },
            onHideFullScreen = {
                fullscreenContainer?.let { container ->
                    container.removeAllViews()
                    (activity.window.decorView as ViewGroup).removeView(container)
                }
                fullscreenContainer = null
                fullScreenState = false

                webView.onResume()
                webView.resumeTimers()
                webView.resumeVideo()
            },
        )

    private val webViewClient =
        TuripWebViewClient(
            onLoadingStarted = { loadingState = true },
            onLoadingFinished = { loadingState = false },
            onNavigateExternalUrl = navigateToWebViewUrl,
        )

    init {
        webView.webViewClient = webViewClient
        webView.webChromeClient = webChromeClient
    }

    actual fun loadVideo(
        url: String,
        initialSecond: Int,
        onTimeUpdate: (time: Int) -> Unit,
    ) {
        if (url.isNotEmpty() && url != webView.url) {
            if (isError) errorState = false
            if (initialSecond > 0) playbackRestoringState = true
            videoManager.loadVideo(
                url = url,
                initialSecond = initialSecond,
                onTimeUpdate = { second ->
                    onTimeUpdate(second)
                    if (isPlaybackRestoring && second > 0) {
                        playbackRestoringState = false
                    }
                },
                onError = {
                    errorState = true
                    playbackRestoringState = false
                },
            )
        }
    }

    actual fun seekTo(seconds: Int) {
        webView.navigateToTimeLine(seconds)
    }

    actual fun restoreTo(seconds: Int) {
        if (seconds <= 0) return
        playbackRestoringState = true
        webView.navigateToTimeLine(seconds)
    }

    actual fun canHandleBack(): Boolean = webView.canGoBack() || isFullScreen

    actual fun handleBack() {
        when {
            isFullScreen -> webChromeClient.onHideCustomView()
            webView.canGoBack() -> webView.goBack()
        }
    }

    actual fun clear() {
        fullscreenContainer?.let { container ->
            container.removeAllViews()
            (activity.window.decorView as ViewGroup).removeView(container)
        }
        fullscreenContainer = null
        playbackRestoringState = false
        videoManager.clear()
        webView.destroy()
    }
}

@Composable
actual fun rememberTripDetailWebViewController(
    baseUrl: String,
    navigateToWebViewUrl: (webUrl: String) -> Unit,
): TripDetailWebViewController {
    val context = LocalContext.current
    val activity = LocalActivity.current as? Activity
    return remember {
        val webView = WebView(context).apply { applyVideoSettings() }
        TripDetailWebViewController(
            webView = webView,
            videoManager = VideoManager(webView, baseUrl),
            navigateToWebViewUrl = navigateToWebViewUrl,
            activity = requireNotNull(activity) { "TripDetailWebViewController requires an Activity" },
        )
    }
}

@Composable
actual fun HandleFullScreenWindowLaunchedEffect(isFullScreen: Boolean) {
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

private class TuripWebChromeClient(
    private val onShowFullScreen: (video: View) -> Unit,
    private val onHideFullScreen: () -> Unit,
) : WebChromeClient() {
    private var customView: View? = null
    private var customViewCallBack: CustomViewCallback? = null

    override fun onShowCustomView(
        view: View?,
        callback: CustomViewCallback?,
    ) {
        if (view == null || customView != null) {
            callback?.onCustomViewHidden()
            return
        }

        customView = view
        customViewCallBack = callback

        customView?.let { onShowFullScreen(it) }
    }

    override fun onHideCustomView() {
        if (customView == null) return

        onHideFullScreen()

        customViewCallBack?.onCustomViewHidden()

        customView = null
        customViewCallBack = null
    }
}

private class TuripWebViewClient(
    private val onLoadingStarted: () -> Unit,
    private val onLoadingFinished: () -> Unit,
    private val onNavigateExternalUrl: (url: String) -> Unit,
) : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?,
    ): Boolean {
        val url: String = request?.url.toString()
        return when {
            url.startsWith(TARGET_URL_PREFIX) -> {
                false
            }

            !url.startsWith(SECURE_URL) -> {
                true
            }

            else -> {
                onNavigateExternalUrl(url)
                true
            }
        }
    }

    override fun onPageStarted(
        view: WebView?,
        url: String?,
        favicon: Bitmap?,
    ) {
        super.onPageStarted(view, url, favicon)
        onLoadingStarted()
    }

    override fun onPageFinished(
        view: WebView?,
        url: String?,
    ) {
        super.onPageFinished(view, url)
        onLoadingFinished()
    }

    companion object {
        private const val TARGET_URL_PREFIX = "https://www.youtube.com/"
        private const val SECURE_URL = "https://"
    }
}

internal class VideoManager(
    private val webView: WebView,
    private val baseUrl: String,
) {
    private var isInitialized = false
    private var initialSecond: Int = 0
    private var onTimeUpdateCallback: (Int) -> Unit = {}
    private var onErrorCallback: () -> Unit = {}

    fun loadVideo(
        url: String,
        initialSecond: Int,
        onTimeUpdate: (time: Int) -> Unit,
        onError: () -> Unit,
    ) {
        this.initialSecond = initialSecond.coerceAtLeast(0)
        this.onTimeUpdateCallback = onTimeUpdate
        this.onErrorCallback = onError

        webView.apply {
            if (!isInitialized) {
                addJavascriptInterface(
                    WebViewVideoBridge(
                        videoId = extractVideoId(url),
                        onPlayerReadyCallback = {
                            if (this@VideoManager.initialSecond > 0) {
                                webView.post {
                                    webView.navigateToTimeLine(this@VideoManager.initialSecond)
                                }
                            }
                        },
                        onTimeUpdateCallback = { second ->
                            webView.post {
                                this@VideoManager.onTimeUpdateCallback(second)
                            }
                        },
                        onErrorCallback = {
                            webView.post {
                                this@VideoManager.onErrorCallback()
                            }
                        },
                    ),
                    BRIDGE_NAME_IN_JS_FILE,
                )
                isInitialized = true
            }

            loadDataWithBaseURL(
                baseUrl,
                readLocalHtml(LOCAL_HTML_FILE_NAME),
                DEFAULT_MIME_TYPE,
                DEFAULT_ENCODING,
                null,
            )
        }
    }

    private fun readLocalHtml(fileName: String): String =
        webView.context.assets
            .open(fileName)
            .bufferedReader()
            .use { it.readText() }

    fun clear() {
        webView.removeJavascriptInterface(BRIDGE_NAME_IN_JS_FILE)
    }

    companion object {
        private const val BRIDGE_NAME_IN_JS_FILE: String = "videoBridge"
        private const val LOCAL_HTML_FILE_NAME: String = "iframe.html"
        private const val DEFAULT_ENCODING: String = "utf-8"
        private const val DEFAULT_MIME_TYPE: String = "text/html"
    }
}

private class WebViewVideoBridge(
    private val videoId: String,
    private val onPlayerReadyCallback: () -> Unit,
    private val onTimeUpdateCallback: (time: Int) -> Unit,
    private val onErrorCallback: () -> Unit,
) {
    @JavascriptInterface
    fun videoId(): String = videoId

    @JavascriptInterface
    fun onPlayerReady() {
        onPlayerReadyCallback()
    }

    @JavascriptInterface
    fun onTimeUpdate(seconds: Int) {
        val safeSeconds = seconds.coerceAtLeast(0)
        onTimeUpdateCallback(safeSeconds)
    }

    @JavascriptInterface
    fun onPlayerError(errorCode: Int) {
        if (errorCode == 101 || errorCode == 150) {
            onErrorCallback()
        }
    }

    @JavascriptInterface
    fun seekToSecond(seconds: Int): Int = seconds
}

@SuppressLint("SetJavaScriptEnabled")
private fun WebView.applyVideoSettings() {
    settings.apply {
        javaScriptEnabled = true
        javaScriptCanOpenWindowsAutomatically = true
        domStorageEnabled = true
        allowFileAccess = false
        mediaPlaybackRequiresUserGesture = false

        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
}

private fun WebView.navigateToTimeLine(seconds: Int) {
    evaluateJavascript(
        "seekToSecond(${seconds.coerceAtLeast(0)});",
        null,
    )
}

private fun WebView.resumeVideo() {
    val script =
        """
        (function() {
            if (typeof player !== 'undefined' && player.getPlayerState) {
                var state = player.getPlayerState();
                if (state === 1 || state === 3) {
                    player.playVideo();
                }
            }
        })();
        """.trimIndent()
    evaluateJavascript(script, null)
}

private fun extractVideoId(videoUrl: String): String {
    val patterns =
        listOf(
            Regex("youtu\\.be/([^?&/]+)"),
            Regex("[?&]v=([^?&/]+)"),
            Regex("/embed/([^?&/]+)"),
            Regex("/shorts/([^?&/]+)"),
        )
    return patterns.firstNotNullOfOrNull { pattern ->
        pattern.find(videoUrl)?.groupValues?.getOrNull(1)
    } ?: videoUrl.substringAfterLast('/').substringBefore('?').substringBefore('&')
}
