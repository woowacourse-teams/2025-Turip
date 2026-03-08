package com.on.turip.ui.compose.turip.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import javax.inject.Inject

class MyTuripNavKeyProvider @Inject constructor() : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(MyTuripNavKey::class, MyTuripNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        myTuripScreen(navigator)
    }
}
