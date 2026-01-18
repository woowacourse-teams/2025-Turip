package com.on.turip.ui.compose.trip.webview

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView

@SuppressLint("SetJavaScriptEnabled")
fun WebView.applyVideoSettings() {
    this.settings.apply {
        javaScriptEnabled = true
        javaScriptCanOpenWindowsAutomatically = true
        domStorageEnabled = true
        allowFileAccess = false
        mediaPlaybackRequiresUserGesture = false

        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
}

fun WebView.navigateToTimeLine(seconds: Int) {
    evaluateJavascript(
        "seekToSecond(${seconds.coerceAtLeast(0)});",
        null,
    )
}

/**
 * 재생 중이거나 버퍼링이라면 재생을 재개한다.
 */
fun WebView.resumeVideo() {
    // Youtube Player API state
    // -1 : 시작 되지 않음, 0 : 재생 완료, 1 : 재생 중, 2 : 일시 중지, 3 : 버퍼링
    val script =
        """
        (function() {
            if (typeof player !== 'undefined' && player.getPlayerState) {
                var state = player.getPlayerState();
                if (state === 1 || state === 3) {
                    player.playVideo();
                }
            }
        })();
        """.trimIndent()
    evaluateJavascript(script, null)
}
