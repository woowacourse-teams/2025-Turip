package com.on.turip.ui.search.detail.webview

import android.annotation.SuppressLint
import android.webkit.WebView
import com.on.turip.ui.search.detail.webview.VideoRegex.patternShortUrl
import com.on.turip.ui.search.detail.webview.VideoRegex.patternVParam

@SuppressLint("SetJavaScriptEnabled")
fun WebView.applyVideoSettings() {
    this.settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        allowFileAccess = false
    }
}

private object VideoRegex {
    val patternVParam = "v=([a-zA-Z0-9_-]{11})(?:&|$)".toRegex()
    val patternShortUrl = "youtu\\.be/([a-zA-Z0-9_-]{11})".toRegex()
}

fun String.extractVideoId(): String {
    val matches = patternVParam.find(this) ?: patternShortUrl.find(this)
    return matches?.groups?.get(1)?.value ?: ""
}
