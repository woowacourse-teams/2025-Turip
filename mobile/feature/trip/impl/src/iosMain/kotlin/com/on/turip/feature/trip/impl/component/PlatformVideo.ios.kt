package com.on.turip.feature.trip.impl.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.on.turip.feature.trip.impl.TripDetailWebViewController

@Composable
internal actual fun PlatformVideo(
    webViewController: TripDetailWebViewController,
    modifier: Modifier,
) {
    UIKitView(
        factory = { webViewController.webView },
        modifier = modifier,
    )
}
