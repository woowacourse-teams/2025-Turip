package com.on.turip.ui.compose.common.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.on.turip.domain.ErrorEvent

@Composable
fun ErrorHandlingContainer(
    networkError: Boolean,
    serverError: Boolean,
    onRetryClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Crossfade(targetState = networkError || serverError) { hasError ->
        if (hasError) {
            ErrorScreen(
                errorEvent = if (networkError) ErrorEvent.NETWORK_ERROR else ErrorEvent.UNEXPECTED_PROBLEM,
                onRetryClick = onRetryClick,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            content()
        }
    }
}
