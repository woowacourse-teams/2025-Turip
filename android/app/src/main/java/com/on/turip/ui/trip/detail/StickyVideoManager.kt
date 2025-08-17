package com.on.turip.ui.trip.detail

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.WebView
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.trip.detail.webview.WebViewVideoBridge

class StickyVideoManager(
    private val originalVideoContainer: CardView,
    private val stickyVideoContainer: CardView,
    private val nestedScrollView: NestedScrollView,
    private val webView: WebView,
) {
    private var stickyThreshold = 0
    private var currentVideoUrl: String? = null
    private var isVideoLoaded = false
    private var isVideoInStickyMode = false

    fun initialize() {
        nestedScrollView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    nestedScrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    calculateStickyThreshold()
                }
            },
        )

        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            handleStickyVideo(scrollY)
        }
    }

    private fun calculateStickyThreshold() {
        originalVideoContainer.post {
            val location = IntArray(2)
            originalVideoContainer.getLocationInWindow(location)

            val scrollViewLocation = IntArray(2)
            nestedScrollView.getLocationInWindow(scrollViewLocation)

            stickyThreshold = location[1] - scrollViewLocation[1]
        }
    }

    private fun handleStickyVideo(scrollY: Int) {
        if (!isVideoLoaded || currentVideoUrl.isNullOrEmpty()) {
            return
        }

        if (scrollY >= stickyThreshold && !isVideoInStickyMode) {
            showStickyVideo()
            isVideoInStickyMode = true
        } else if (scrollY < stickyThreshold && isVideoInStickyMode) {
            hideStickyVideo()
            isVideoInStickyMode = false
        }
    }

    private fun showStickyVideo() {
        val layoutParams = webView.layoutParams

        (webView.parent as? ViewGroup)?.removeView(webView)

        stickyVideoContainer.addView(webView, layoutParams)

        originalVideoContainer.visibility = View.INVISIBLE

        stickyVideoContainer.visibility = View.VISIBLE
        stickyVideoContainer.alpha = 0f
        stickyVideoContainer
            .animate()
            .alpha(1f)
            .setDuration(200)
            .start()
    }

    private fun hideStickyVideo() {
        val layoutParams = webView.layoutParams

        stickyVideoContainer.removeView(webView)

        originalVideoContainer.addView(webView, layoutParams)
        originalVideoContainer.visibility = View.VISIBLE

        stickyVideoContainer
            .animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                stickyVideoContainer.visibility = View.GONE
            }.start()
    }

    fun loadVideo(
        url: String,
        onError: () -> Unit,
    ) {
        currentVideoUrl = url
        isVideoLoaded = true

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
        private const val BRIDGE_NAME_IN_JS_FILE = "videoBridge"
        private const val LOAD_URL_FILE_PATH = "file:///android_asset/iframe.html"
    }
}
