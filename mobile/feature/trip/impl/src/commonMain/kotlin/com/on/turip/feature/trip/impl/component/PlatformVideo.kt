package com.on.turip.feature.trip.impl.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.on.turip.feature.trip.impl.TripDetailWebViewController

@Composable
internal expect fun PlatformVideo(
    webViewController: TripDetailWebViewController,
    modifier: Modifier = Modifier,
)
