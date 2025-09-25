package com.on.turip.ui.trip.detail.webview

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView

@SuppressLint("SetJavaScriptEnabled")
fun WebView.applyVideoSettings() {
    this.settings.apply {
        javaScriptEnabled = true
        javaScriptCanOpenWindowsAutomatically = true
        domStorageEnabled = true
        allowFileAccess = false
        mediaPlaybackRequiresUserGesture = false

        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
}

fun WebView.navigateToTimeLine(seconds: Int) {
    evaluateJavascript(
        "seekToSecond($seconds);",
        null,
    )
}
