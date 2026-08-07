package com.on.turip.feature.main.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator

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
