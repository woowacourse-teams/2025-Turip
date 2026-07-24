package com.on.turip.feature.trip.impl.component

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.on.turip.feature.trip.impl.TripDetailWebViewController

@Composable
internal actual fun PlatformVideo(
    webViewController: TripDetailWebViewController,
    modifier: Modifier,
) {
    AndroidView(
        factory = {
            webViewController.webView.apply {
                (parent as? ViewGroup)?.removeView(this)
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        },
        update = { view ->
            view.visibility =
                if (webViewController.isFullScreen) View.INVISIBLE else View.VISIBLE
        },
        modifier = modifier,
    )
}
