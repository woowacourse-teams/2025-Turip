package com.on.turip.ui.compose.splash.navigation

import android.app.Activity
import android.content.Context
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import javax.inject.Inject

class SplashNavKeyProvider @Inject constructor(
    @ActivityContext private val context: Context,
) : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(SplashNavKey::class, SplashNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        splashScreen(navigator, (context as Activity).intent?.dataString)
    }
}
