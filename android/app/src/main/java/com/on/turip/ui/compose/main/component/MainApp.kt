package com.on.turip.ui.compose.main.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.on.turip.R
import com.on.turip.navigation.Navigator
import com.on.turip.navigation.rememberNavigationState
import com.on.turip.navigation.toEntries
import com.on.turip.ui.common.TuripNavigationBar
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.home.navigation.HomeNavKey
import com.on.turip.ui.compose.main.navigation.SavedStateConfigurationProvider
import com.on.turip.ui.compose.main.navigation.TopLevel
import com.on.turip.ui.compose.main.navigation.appScreens
import com.on.turip.ui.compose.main.navigation.rememberDialogAppState

@Composable
fun MainApp(savedStateConfigurationProvider: SavedStateConfigurationProvider) {
    val navigationState =
        rememberNavigationState(
            startKey = HomeNavKey,
            topLevelKeys = TopLevel.routes.keys,
            configuration = savedStateConfigurationProvider.savedStateConfiguration,
        )

    val appState = rememberDialogAppState(navigationState = navigationState)
    val navigator = remember { Navigator(appState.navigationState) }

    var canExit: Boolean by rememberSaveable { mutableStateOf(false) }
    val isTopLevelRoot: Boolean = appState.navigationState.isTopLevelKey

    val exit: String = stringResource(R.string.main_double_back_pressed_to_exit)

    val animatedSnackbarBottomPadding: Dp by animateDpAsState(
        targetValue = appState.snackbarBottomPadding,
        animationSpec = tween(durationMillis = 220),
    )

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

    LaunchedEffect(appState.navigationState.currentTopLevelKey) {
        appState.snackbarDelegate.dismissCurrentSnackbar()
    }

    BackHandler(
        enabled = isTopLevelRoot && !canExit,
        onBack = { canExit = true },
    )

    TuripTheme {
        Scaffold(
            bottomBar = {
                if (appState.shouldShowBottomBar) {
                    TuripNavigationBar(
                        items = TopLevel.routes,
                        selectedKey = appState.currentScreenKey,
                        onSelectedKeyChange = navigator::navigate,
                    )
                }
            },
            snackbarHost = {
                TuripSnackbar(
                    snackbarHostState = appState.snackbarHostState,
                    modifier = Modifier.padding(bottom = animatedSnackbarBottomPadding),
                )
            },
            contentWindowInsets = WindowInsets.systemBars,
        ) { paddingValues ->
            CompositionLocalProvider(
                LocalSnackbarDelegate provides appState.snackbarDelegate,
            ) {
                NavDisplay(
                    entries =
                        appState.navigationState.toEntries { key ->
                            entryProvider {
                                appScreens(navigator, savedStateConfigurationProvider.providers)
                            }.invoke(key)
                        },
                    onBack = navigator::goBack,
                    modifier =
                        Modifier
                            .padding(paddingValues)
                            .consumeWindowInsets(paddingValues)
                            .background(TuripTheme.colors.container),
                    transitionSpec = {
                        ContentTransform(
                            targetContentEnter = fadeIn(animationSpec = tween(durationMillis = 300)),
                            initialContentExit = fadeOut(animationSpec = tween(durationMillis = 300)),
                        )
                    },
                    popTransitionSpec = {
                        ContentTransform(
                            targetContentEnter = fadeIn(animationSpec = tween(durationMillis = 300)),
                            initialContentExit = fadeOut(animationSpec = tween(durationMillis = 300)),
                        )
                    },
                )
            }
        }
    }
}
