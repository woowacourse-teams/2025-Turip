package com.on.turip.ui.compose.designsystem.component

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
    if (networkError || serverError) {
        ErrorScreen(
            errorEvent = if (networkError) ErrorEvent.NETWORK_ERROR else ErrorEvent.UNEXPECTED_PROBLEM,
            onRetryClick = onRetryClick,
            modifier = Modifier.fillMaxSize(),
        )
    } else {
        content()
    }
}
