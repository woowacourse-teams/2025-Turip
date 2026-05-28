package com.on.turip.feature.bookmark.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.bookmark.api.BookmarkNavKey
import com.on.turip.feature.bookmark.impl.BookmarkScreen
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.trip.api.TripDetailNavKey

fun EntryProviderScope<NavKey>.bookmarkScreen(navigator: Navigator) {
    entry<BookmarkNavKey> { _ ->
        BookmarkScreen(
            onNavigateToLogin = { navigator.replace(LoginNavKey()) },
            onNavigateToTripDetail = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
            onBackClick = { navigator.goBack() },
        )
    }
}
