package com.on.turip.ui.compose.main.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator

fun EntryProviderScope<NavKey>.appScreens(
    navigator: Navigator,
    providers: List<NavKeyProvider>,
) {
    providers.forEach { provider ->
        with(provider) {
            registerScreens(navigator)
        }
    }
}
