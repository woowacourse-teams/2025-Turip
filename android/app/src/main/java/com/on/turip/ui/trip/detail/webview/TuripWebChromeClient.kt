package com.on.turip.ui.trip.detail.webview

import android.view.View
import android.webkit.WebChromeClient
import android.widget.FrameLayout

class TuripWebChromeClient(
    private val fullScreenView: FrameLayout,
    private val onEnterFullScreen: () -> Unit,
    private val onExitFullScreen: () -> Unit,
) : WebChromeClient() {
    private var customView: View? = null
    private var customViewCallBack: CustomViewCallback? = null

    override fun onShowCustomView(
        view: View?,
        callback: CustomViewCallback?,
    ) {
        if (customView != null) {
            callback?.onCustomViewHidden()
            return
        }

        customView = view
        customViewCallBack = callback

        fullScreenView.apply {
            visibility = View.VISIBLE
            addView(
                view,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            )
        }

        onEnterFullScreen()
    }

    override fun onHideCustomView() {
        fullScreenView.apply {
            removeAllViews()
            visibility = View.GONE
        }

        customView = null
        customViewCallBack?.onCustomViewHidden()
        customViewCallBack = null

        onExitFullScreen()
    }

    fun isFullScreen(): Boolean = customView != null
}
