package com.on.turip.ui.trip.detail.webview

import android.annotation.SuppressLint
import android.webkit.WebView

@SuppressLint("SetJavaScriptEnabled")
fun WebView.applyVideoSettings() {
    this.settings.apply {
        javaScriptEnabled = true
        javaScriptCanOpenWindowsAutomatically = true
        domStorageEnabled = true
        allowFileAccess = false
    }
}
