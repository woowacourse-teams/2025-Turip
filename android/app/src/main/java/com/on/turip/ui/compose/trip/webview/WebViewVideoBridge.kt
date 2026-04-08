package com.on.turip.ui.compose.trip.webview

import android.webkit.JavascriptInterface

class WebViewVideoBridge(
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
