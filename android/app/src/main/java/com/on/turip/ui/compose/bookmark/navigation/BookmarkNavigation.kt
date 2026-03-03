package com.on.turip.ui.compose.bookmark.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.Navigator
import com.on.turip.ui.compose.bookmark.BookmarkContentListScreen
import com.on.turip.ui.compose.login.navigation.LoginNavKey
import com.on.turip.ui.compose.trip.navigation.TripDetailNavKey

fun EntryProviderScope<NavKey>.bookmarkContentListScreen(navigator: Navigator) {
    entry<BookmarkNavKey> {
        BookmarkContentListScreen(
            onBack = navigator::goBack,
            onNavigateToLogin = { navigator.navigate(LoginNavKey) },
            onNavigateToContent = { turipId: Long ->
                navigator.navigate(TripDetailNavKey(turipId))
            },
        )
    }
}
