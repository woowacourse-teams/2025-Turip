package com.on.turip.ui.compose.trip

import android.view.View
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class TripDetailWebViewState {
    var isLoading by mutableStateOf(false)
        private set

    var isFullScreen by mutableStateOf(false)
        private set

    var isError by mutableStateOf(false)
        private set

    var fullScreenVideo: View? = null
        private set

    fun showFullScreen(view: View) {
        fullScreenVideo = view
        isFullScreen = true
    }

    fun hideFullScreen() {
        fullScreenVideo = null
        isFullScreen = false
    }

    fun updateWebViewError(isError: Boolean) {
        this.isError = isError
    }

    fun loadingStarted() {
        isLoading = true
    }

    fun loadingFinished() {
        isLoading = false
    }
}
