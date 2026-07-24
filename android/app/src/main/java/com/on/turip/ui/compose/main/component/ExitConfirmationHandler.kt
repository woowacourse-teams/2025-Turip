package com.on.turip.ui.compose.main.component

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.on.turip.R
import com.on.turip.ui.compose.main.navigation.TuripAppState

@Composable
fun ExitConfirmationHandler(appState: TuripAppState) {
    val activity = LocalActivity.current
    var canExit: Boolean by remember { mutableStateOf(false) }
    val isTopLevelRoot: Boolean = appState.navigationState.isTopLevelKey

    val exit: String = stringResource(R.string.main_double_back_pressed_to_exit)

    LaunchedEffect(canExit, isTopLevelRoot) {
        if (!canExit) return@LaunchedEffect

        if (!isTopLevelRoot) {
            canExit = false
            appState.snackbarDelegate.dismissCurrentSnackbar()
            return@LaunchedEffect
        }

        appState.snackbarDelegate.showSnackbar(
            message = exit,
            onDismiss = { canExit = false },
        )
    }

    LaunchedEffect(appState.navigationState.currentKey) {
        appState.snackbarDelegate.dismissCurrentSnackbar()
    }

    BackHandler(
        enabled = isTopLevelRoot,
        onBack = {
            if (canExit) {
                activity?.finish()
            } else {
                canExit = true
            }
        },
    )
}
