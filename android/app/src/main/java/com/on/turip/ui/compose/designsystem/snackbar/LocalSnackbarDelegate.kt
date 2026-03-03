package com.on.turip.ui.compose.designsystem.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import com.on.turip.ui.compose.designsystem.component.TuripSnackbarVisuals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val LocalSnackbarDelegate =
    compositionLocalOf<SnackbarDelegate> {
        error("No SnackbarDelegate provided")
    }

@Stable
class SnackbarDelegate(
    val snackbarHostState: SnackbarHostState,
    val coroutineScope: CoroutineScope,
) {
    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = actionLabel == null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onDismiss: () -> Unit = {},
        onAction: () -> Unit = {},
    ) {
        coroutineScope.launch {
            val visuals =
                TuripSnackbarVisuals(
                    message = message,
                    actionLabel = actionLabel,
                    withDismissAction = withDismissAction,
                    duration = duration,
                )

            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(visuals)

            when (result) {
                SnackbarResult.Dismissed -> onDismiss()
                SnackbarResult.ActionPerformed -> onAction()
            }
        }
    }
}
