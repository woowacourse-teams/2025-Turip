package com.on.turip.ui.compose.turip.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.Navigator
import com.on.turip.ui.compose.login.navigation.LoginNavKey
import com.on.turip.ui.compose.turip.MyTuripScreen
import com.on.turip.ui.compose.turipdetail.navigation.TuripDetailNavKey

fun EntryProviderScope<NavKey>.myTuripScreen(navigator: Navigator) {
    entry<MyTuripNavKey> {
        MyTuripScreen(
            onNavigateToTuripPlace = { navigator.navigate(TuripDetailNavKey) },
            onNavigateToLogin = { navigator.navigate(LoginNavKey) },
        )
    }
}
