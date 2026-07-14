package com.on.turip.feature.turip.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.turip.api.MyTuripNavKey
import com.on.turip.feature.turip.impl.MyTuripScreen
import com.on.turip.feature.turipdetail.api.TuripDetailNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class MyTuripNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(MyTuripNavKey::class, MyTuripNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<MyTuripNavKey> {
            MyTuripScreen(
                onNavigateToTuripDetail = { turipId -> navigator.navigate(TuripDetailNavKey(turipId)) },
                onNavigateToLogin = { navigator.goWithAllClear(LoginNavKey()) },
            )
        }
    }
}
