package com.on.turip.ui.compose.common.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
    // 에러가 있는 경우
    AnimatedVisibility(
        visible = networkError || serverError,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        ErrorScreen(
            errorEvent = if (networkError) ErrorEvent.NETWORK_ERROR else ErrorEvent.UNEXPECTED_PROBLEM,
            onRetryClick = onRetryClick,
            modifier = Modifier.fillMaxSize(),
        )
    }

    // 에러가 없는 경우 일반 콘텐츠
    AnimatedVisibility(
        visible = !(networkError || serverError),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        content()
    }
}
