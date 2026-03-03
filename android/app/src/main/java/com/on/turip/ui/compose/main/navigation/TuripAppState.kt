package com.on.turip.ui.compose.main.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.NavigationState
import com.on.turip.ui.compose.designsystem.snackbar.SnackbarDelegate
import kotlinx.coroutines.CoroutineScope

@Stable
class TuripAppState(
    val snackbarDelegate: SnackbarDelegate,
    val navigationState: NavigationState,
) {
    val snackbarHostState: SnackbarHostState = snackbarDelegate.snackbarHostState

    val currentScreenKey: NavKey
        get() = navigationState.currentKey

    val shouldShowBottomBar: Boolean
        get() = navigationState.currentKey in TopLevel.routes.keys
}

@Composable
fun rememberDialogAppState(
    navigationState: NavigationState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): TuripAppState =
    remember(snackbarHostState, coroutineScope, navigationState) {
        TuripAppState(
            snackbarDelegate = SnackbarDelegate(snackbarHostState, coroutineScope),
            navigationState = navigationState,
        )
    }
