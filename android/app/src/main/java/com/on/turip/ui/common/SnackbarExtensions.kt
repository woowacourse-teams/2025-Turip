package com.on.turip.ui.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

suspend fun SnackbarHostState.showSnackbarWithAction(
    message: String,
    actionLabel: String,
    duration: SnackbarDuration = SnackbarDuration.Short,
    onAction: () -> Unit,
    onDismiss: (() -> Unit)? = null,
) {
    when (
        showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration,
        )
    ) {
        SnackbarResult.ActionPerformed -> onAction()
        SnackbarResult.Dismissed -> onDismiss?.invoke()
    }
}

inline fun SnackbarHostState.dismissAndExecute(crossinline action: () -> Unit) {
    if (currentSnackbarData != null) currentSnackbarData?.dismiss()
    action()
}
