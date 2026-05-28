package com.on.turip.feature.mypage.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.bookmark.api.BookmarkNavKey
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.mypage.api.MyPageNavKey
import com.on.turip.feature.mypage.impl.MyPageScreen
import com.on.turip.feature.trip.api.TripDetailNavKey

fun EntryProviderScope<NavKey>.myPageScreen(navigator: Navigator) {
    entry<MyPageNavKey> {
        MyPageScreen(
            onNavigateToAllBookmarks = { navigator.navigate(BookmarkNavKey) },
            onNavigateToContent = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
            onNavigateToLogin = { navigator.navigate(LoginNavKey(deepLinkUrl = null)) },
        )
    }
}
