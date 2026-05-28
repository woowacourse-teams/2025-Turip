package com.on.turip.feature.trip.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.trip.api.TripDetailNavKey
import com.on.turip.feature.trip.impl.TripDetailScreen

fun EntryProviderScope<NavKey>.tripDetailScreen(navigator: Navigator) {
    entry<TripDetailNavKey> { key ->
        TripDetailScreen(
            contentId = key.contentId,
            onNavigateBack = { navigator.goBack() },
            onNavigateToLogin = { navigator.navigate(LoginNavKey(deepLinkUrl = null)) },
        )
    }
}
