package com.on.turip.ui.search.detail.webview

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView

@SuppressLint("SetJavaScriptEnabled")
fun WebView.applyVideoSettings() {
    this.settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true

        setSupportMultipleWindows(false)
        allowFileAccess = false
        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
    }
}

fun String.extractVideoId(): String {
    val startIdx = this.indexOf("v=")
    if (startIdx == -1) return ""
    return this.substring(startIdx + 2)
}
