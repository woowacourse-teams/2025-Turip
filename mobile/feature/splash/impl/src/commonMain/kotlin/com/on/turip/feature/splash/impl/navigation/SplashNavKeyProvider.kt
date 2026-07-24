package com.on.turip.feature.splash.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.common.deeplink.StartupNavigationArbiter
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.splash.api.SplashNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class SplashNavKeyProvider(
    private val startupNavigationArbiter: StartupNavigationArbiter,
) : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(SplashNavKey::class, SplashNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        splashScreen(navigator, startupNavigationArbiter)
    }
}
