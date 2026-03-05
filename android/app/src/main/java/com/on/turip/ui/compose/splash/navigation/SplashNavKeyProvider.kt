package com.on.turip.ui.compose.splash.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import jakarta.inject.Inject
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class SplashNavKeyProvider @Inject constructor() : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(SplashNavKey::class, SplashNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        splashScreen(navigator)
    }
}
