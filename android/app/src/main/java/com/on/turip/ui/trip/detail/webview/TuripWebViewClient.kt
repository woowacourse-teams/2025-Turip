package com.on.turip.ui.trip.detail.webview

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class TuripWebViewClient(
    private val onLoadingStarted: () -> Unit,
    private val onLoadingFinished: () -> Unit,
    private val onNavigateExternalUrl: (String) -> Unit,
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
