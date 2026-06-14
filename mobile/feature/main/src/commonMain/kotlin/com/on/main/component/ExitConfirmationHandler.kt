package com.on.turip.feature.main.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.on.turip.core.common.Platform
import com.on.turip.core.common.currentPlatform
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.main_double_back_pressed_to_exit
import com.on.turip.feature.main.navigation.TuripAppState
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ExitConfirmationHandler(
    appState: TuripAppState,
) {
    var canExit: Boolean by rememberSaveable { mutableStateOf(false) }
    val isTopLevelRoot: Boolean = appState.navigationState.isTopLevelKey
    val exitMessage: String = stringResource(Res.string.main_double_back_pressed_to_exit)

    LaunchedEffect(canExit, isTopLevelRoot) {
        if (!canExit) return@LaunchedEffect

        if (!isTopLevelRoot) {
            canExit = false
            appState.snackbarDelegate.dismissCurrentSnackbar()
            return@LaunchedEffect
        }

        appState.snackbarDelegate.showSnackbar(
            message = exitMessage,
            onDismiss = { canExit = false },
        )
    }

    LaunchedEffect(appState.navigationState.currentTopLevelKey) {
        appState.snackbarDelegate.dismissCurrentSnackbar()
    }

    NavigationBackHandler(
        state = rememberNavigationEventState(currentInfo = NavigationEventInfo.None),
        isBackEnabled = currentPlatform == Platform.Android && isTopLevelRoot && !canExit,
        onBackCompleted = { canExit = true },
    )
}
