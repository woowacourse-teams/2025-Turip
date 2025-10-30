package com.on.turip.ui.trip.detail

import android.webkit.WebView
import com.on.turip.BuildConfig
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.trip.detail.webview.WebViewVideoBridge

class VideoManager(
    private val webView: WebView,
) {
    private var isInitialized = false

    fun loadVideo(
        url: String,
        onError: () -> Unit,
    ) {
        webView.apply {
            if (!isInitialized) {
                addJavascriptInterface(
                    WebViewVideoBridge(
                        TuripUrlConverter.extractVideoId(url),
                    ) { onError() },
                    BRIDGE_NAME_IN_JS_FILE,
                )
                isInitialized = true
            }

            loadDataWithBaseURL(
                getBaseUrl(),
                readLocalHtml(LOCAL_HTML_FILE_NAME),
                DEFAULT_MIME_TYPE,
                DEFAULT_ENCODING,
                null,
            )
        }
    }

    private fun getBaseUrl(): String = if (BuildConfig.DEBUG) BuildConfig.debug_base_url else BuildConfig.release_base_url

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
