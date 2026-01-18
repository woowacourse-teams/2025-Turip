package com.on.turip.ui.compose.trip.webview

import android.view.View
import android.webkit.WebChromeClient

class TuripWebChromeClient(
    private val onShowFullScreen: (video: View) -> Unit,
    private val onHideFullScreen: () -> Unit,
) : WebChromeClient() {
    private var customView: View? = null
    private var customViewCallBack: CustomViewCallback? = null

    override fun onShowCustomView(
        view: View?,
        callback: CustomViewCallback?,
    ) {
        if (view == null || customView != null) {
            callback?.onCustomViewHidden()
            return
        }

        customView = view
        customViewCallBack = callback

        customView?.let { onShowFullScreen(it) }
    }

    override fun onHideCustomView() {
        if (customView == null) return

        onHideFullScreen()

        customViewCallBack?.onCustomViewHidden()

        customView = null
        customViewCallBack = null
    }
}
