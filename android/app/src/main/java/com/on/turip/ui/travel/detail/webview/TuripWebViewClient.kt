package com.on.turip.ui.travel.detail.webview

import android.graphics.Bitmap
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class TuripWebViewClient(
    private val progressBar: View,
) : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?,
    ): Boolean {
        val url = request?.url.toString()
        return when {
            url.startsWith(TARGET_URL_PREFIX) -> false
            else -> true
        }
    }

    override fun onPageStarted(
        view: WebView?,
        url: String?,
        favicon: Bitmap?,
    ) {
        super.onPageStarted(view, url, favicon)
        progressBar.visibility = View.VISIBLE
    }

    override fun onPageFinished(
        view: WebView?,
        url: String?,
    ) {
        super.onPageFinished(view, url)
        progressBar.visibility = View.GONE
    }

    companion object {
        private const val TARGET_URL_PREFIX = "https://www.youtube.com/"
    }
}
