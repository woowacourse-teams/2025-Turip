package com.on.turip.ui.compose.designsystem.snackbar

import androidx.annotation.DrawableRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.ui.compose.designsystem.component.TuripSnackbarVisuals
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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
    private var snackbarJob: Job? = null
    var bottomPadding: Dp by mutableStateOf(0.dp)
        private set

    fun updateBottomPadding(padding: Dp) {
        bottomPadding = padding
    }

    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = actionLabel == null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onDismiss: () -> Unit = {},
        onAction: () -> Unit = {},
        @DrawableRes iconRes: Int? = null,
    ) {
        snackbarJob?.cancel()
        snackbarJob =
            coroutineScope.launch {
                val visuals =
                    TuripSnackbarVisuals(
                        message = message,
                        actionLabel = actionLabel,
                        withDismissAction = withDismissAction,
                        duration = duration,
                        iconRes = iconRes,
                    )
                try {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(visuals)

                    when (result) {
                        SnackbarResult.Dismissed -> onDismiss()
                        SnackbarResult.ActionPerformed -> onAction()
                    }
                } catch (e: CancellationException) {
                    onDismiss()
                    throw e
                } finally {
                    snackbarJob = null
                }
            }
    }

    fun dismissCurrentSnackbar() {
        snackbarJob?.cancel()
        snackbarJob = null
        snackbarHostState.currentSnackbarData?.dismiss()
    }
}
