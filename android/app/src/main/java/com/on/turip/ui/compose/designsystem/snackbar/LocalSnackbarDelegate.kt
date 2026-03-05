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
        coroutineScope.launch {
            val visuals =
                TuripSnackbarVisuals(
                    message = message,
                    actionLabel = actionLabel,
                    withDismissAction = withDismissAction,
                    duration = duration,
                    iconRes = iconRes,
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
