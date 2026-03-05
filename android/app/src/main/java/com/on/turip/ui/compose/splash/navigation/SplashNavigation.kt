package com.on.turip.ui.compose.splash.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.Navigator
import com.on.turip.ui.compose.home.navigation.HomeNavKey
import com.on.turip.ui.compose.login.navigation.LoginNavKey
import com.on.turip.ui.compose.splash.SplashScreen

fun EntryProviderScope<NavKey>.splashScreen(navigator: Navigator) {
    entry<SplashNavKey> {
        SplashScreen(
            navigateToHome = { navigator.navigate(HomeNavKey) },
            navigateToLogin = { navigator.goWithAllClear(LoginNavKey) },
            onFinish = navigator::goBack,
        )
    }
}
