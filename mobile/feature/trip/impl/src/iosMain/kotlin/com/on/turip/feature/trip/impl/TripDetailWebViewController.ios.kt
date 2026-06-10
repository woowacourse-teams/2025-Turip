package com.on.turip.feature.trip.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.CoreGraphics.CGRectMake

@Stable
actual class TripDetailWebViewController internal constructor(
    internal val webView: WKWebView,
    private val navigateToWebViewUrl: (webUrl: String) -> Unit,
) {
    private var fullScreenState by mutableStateOf(false)
    actual val isFullScreen: Boolean get() = fullScreenState
    private var loadingState by mutableStateOf(false)
    actual val isLoading: Boolean get() = loadingState
    private var errorState by mutableStateOf(false)
    actual val isError: Boolean get() = errorState
    private var playbackRestoringState by mutableStateOf(false)
    actual val isPlaybackRestoring: Boolean get() = playbackRestoringState

    private var currentUrl: String = ""

    actual fun loadVideo(
        url: String,
        initialSecond: Int,
        onTimeUpdate: (time: Int) -> Unit,
    ) {
        if (url.isEmpty() || url == currentUrl) return
        currentUrl = url
        errorState = false
        loadingState = true
        playbackRestoringState = initialSecond > 0

        val videoId = extractVideoId(url)
        val embedUrl =
            buildString {
                append("https://www.youtube.com/embed/")
                append(videoId)
                append("?playsinline=1&autoplay=1&controls=1")
                if (initialSecond > 0) append("&start=$initialSecond")
            }

        val nsUrl = NSURL.URLWithString(embedUrl) ?: run {
            errorState = true
            loadingState = false
            playbackRestoringState = false
            return
        }
        webView.loadRequest(NSURLRequest.requestWithURL(nsUrl))
        onTimeUpdate(initialSecond.coerceAtLeast(0))
        loadingState = false
        playbackRestoringState = false
    }

    actual fun seekTo(seconds: Int) {
        val safeSeconds = seconds.coerceAtLeast(0)
        webView.evaluateJavaScript(
            "if (typeof player !== 'undefined' && player.seekTo) { player.seekTo($safeSeconds, true); }",
            completionHandler = null,
        )
    }

    actual fun restoreTo(seconds: Int) {
        if (seconds <= 0) return
        seekTo(seconds)
    }

    actual fun canHandleBack(): Boolean = webView.canGoBack()

    actual fun handleBack() {
        if (webView.canGoBack()) webView.goBack()
    }

    actual fun clear() {
        webView.stopLoading()
        webView.loadHTMLString("", baseURL = null)
    }

    internal fun openExternal(url: String) {
        navigateToWebViewUrl(url)
    }
}

@Composable
@OptIn(ExperimentalForeignApi::class)
actual fun rememberTripDetailWebViewController(
    baseUrl: String,
    navigateToWebViewUrl: (webUrl: String) -> Unit,
): TripDetailWebViewController =
    remember {
        val configuration =
            WKWebViewConfiguration().apply {
                allowsInlineMediaPlayback = true
                mediaTypesRequiringUserActionForPlayback = 0u
            }
        TripDetailWebViewController(
            webView = WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = configuration),
            navigateToWebViewUrl = navigateToWebViewUrl,
        )
    }

@Composable
actual fun HandleFullScreenWindowLaunchedEffect(isFullScreen: Boolean) {
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
