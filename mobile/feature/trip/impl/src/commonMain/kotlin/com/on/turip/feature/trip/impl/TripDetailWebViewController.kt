package com.on.turip.feature.trip.impl

import androidx.compose.runtime.Composable

expect class TripDetailWebViewController {
    val isFullScreen: Boolean
    val isLoading: Boolean
    val isError: Boolean
    val isPlaybackRestoring: Boolean

    fun loadVideo(
        url: String,
        initialSecond: Int,
        onTimeUpdate: (time: Int) -> Unit,
    )

    fun seekTo(seconds: Int)

    fun restoreTo(seconds: Int)

    fun canHandleBack(): Boolean

    fun handleBack()

    fun clear()
}

@Composable
expect fun rememberTripDetailWebViewController(
    baseUrl: String,
    navigateToWebViewUrl: (webUrl: String) -> Unit,
): TripDetailWebViewController

@Composable
expect fun HandleFullScreenWindowLaunchedEffect(isFullScreen: Boolean)
