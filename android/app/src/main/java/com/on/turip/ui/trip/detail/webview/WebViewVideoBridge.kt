package com.on.turip.ui.trip.detail.webview

import android.webkit.JavascriptInterface

class WebViewVideoBridge(
    private val videoId: String,
    private val onError: () -> Unit,
) {
    @JavascriptInterface
    fun videoId(): String = videoId

    @JavascriptInterface
    fun onPlayerError(errorCode: Int) {
        if (errorCode == 101 || errorCode == 150) {
            onError()
        }
    }

    @JavascriptInterface
    fun seekToSecond(seconds: Int): Int = seconds
}
