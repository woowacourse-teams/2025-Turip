package com.on.turip.feature.main.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.on.turip.core.common.Platform
import com.on.turip.core.common.currentPlatform
import com.on.turip.feature.main.navigation.TuripAppState

@Composable
fun ExitConfirmationHandler(appState: TuripAppState) {
    var canExit: Boolean by remember { mutableStateOf(false) }
    val isTopLevelRoot: Boolean = appState.navigationState.isTopLevelKey

    val exit = "TODO: R.string.main_double_back_pressed_to_exit"

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

    NavigationBackHandler(
        state = rememberNavigationEventState(currentInfo = NavigationEventInfo.None),
        isBackEnabled = currentPlatform == Platform.Android && isTopLevelRoot && !canExit,
        onBackCompleted = { canExit = true },
    )
}
