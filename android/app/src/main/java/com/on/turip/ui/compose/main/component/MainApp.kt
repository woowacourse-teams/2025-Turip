package com.on.turip.ui.compose.main.component

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.on.turip.navigation.Navigator
import com.on.turip.navigation.rememberNavigationState
import com.on.turip.navigation.toEntries
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.home.navigation.HomeNavKey
import com.on.turip.ui.compose.main.navigation.SavedStateConfigurationProvider
import com.on.turip.ui.compose.main.navigation.TopLevel
import com.on.turip.ui.compose.main.navigation.appScreens
import com.on.turip.ui.compose.main.navigation.rememberTuripAppState

@Composable
fun MainApp(savedStateConfigurationProvider: SavedStateConfigurationProvider) {
    val navigationState =
        rememberNavigationState(
            startKey = HomeNavKey,
            topLevelKeys = TopLevel.routes.keys,
            configuration = savedStateConfigurationProvider.savedStateConfiguration,
        )

    val appState = rememberTuripAppState(navigationState = navigationState)
    val navigator = remember { Navigator(appState.navigationState) }

    val animatedSnackbarBottomPadding: Dp by animateDpAsState(
        targetValue = appState.snackbarBottomPadding,
        animationSpec = tween(durationMillis = 220),
    )

    val appEntryProvider: (navKey: NavKey) -> NavEntry<NavKey> =
        remember(navigator, savedStateConfigurationProvider.providers) {
            entryProvider {
                appScreens(navigator, savedStateConfigurationProvider.providers)
            }
        }

    val appEntries: List<NavEntry<NavKey>> = appState.navigationState.toEntries(appEntryProvider)

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
                    entries = appEntries,
                    onBack = navigator::goBack,
                    modifier =
                        Modifier
                            .padding(paddingValues)
                            .consumeWindowInsets(paddingValues)
                            .background(TuripTheme.colors.container),
                    transitionSpec = {
                        fadeTransition()
                    },
                    popTransitionSpec = {
                        fadeTransition()
                    },
                )
                ExitConfirmationHandler(appState = appState)
            }
        }
    }
}

private fun fadeTransition(): ContentTransform =
    ContentTransform(
        targetContentEnter = fadeIn(animationSpec = tween(durationMillis = 300)),
        initialContentExit = fadeOut(animationSpec = tween(durationMillis = 300)),
    )
