package com.on.turip.feature.turipdetail.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.turipdetail.api.TuripDetailNavKey
import com.on.turip.feature.turipdetail.impl.TuripDetailScreen

fun EntryProviderScope<NavKey>.turipDetailScreen(navigator: Navigator) {
    entry<TuripDetailNavKey> { key ->
        TuripDetailScreen(
            turipId = key.turipId,
            onNavigateBack = { navigator.goBack() },
            onNavigateToLogin = { navigator.navigate(LoginNavKey(deepLinkUrl = null)) },
        )
    }
}
