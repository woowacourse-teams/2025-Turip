package com.on.turip.ui.trip.detail

import android.webkit.WebView
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.trip.detail.webview.WebViewVideoBridge

class VideoManager(
    private val webView: WebView,
) {
    fun loadVideo(
        url: String,
        onError: () -> Unit,
    ) {
        webView.apply {
            addJavascriptInterface(
                WebViewVideoBridge(
                    TuripUrlConverter.extractVideoId(url),
                ) { onError() },
                BRIDGE_NAME_IN_JS_FILE,
            )
            loadUrl(LOAD_URL_FILE_PATH)
        }
    }

    companion object {
        private const val BRIDGE_NAME_IN_JS_FILE: String = "videoBridge"
        private const val LOAD_URL_FILE_PATH: String = "file:///android_asset/iframe.html"
    }
}
