package com.on.turip.ui.compose.mypage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.Navigator
import com.on.turip.ui.compose.bookmark.navigation.BookmarkNavKey
import com.on.turip.ui.compose.login.navigation.LoginNavKey
import com.on.turip.ui.compose.mypage.MyPageScreen
import com.on.turip.ui.compose.mypage.model.InquiryMail
import com.on.turip.ui.compose.trip.navigation.TripDetailNavKey

fun EntryProviderScope<NavKey>.myPageScreen(
    navigator: Navigator,
    openInquiry: (InquiryMail) -> Unit,
    openPrivacyPolicy: (String) -> Unit,
) {
    entry<MyPageNavKey> {
        MyPageScreen(
            onNavigateToAllBookmarkContents = { navigator.navigate(BookmarkNavKey) },
            onNavigateToContent = { navigator.navigate(TripDetailNavKey) },
            onNavigateToInquiry = openInquiry,
            onNavigateToPrivacyPolicy = openPrivacyPolicy,
            onNavigateToLogin = { navigator.navigate(LoginNavKey) },
        )
    }
}
