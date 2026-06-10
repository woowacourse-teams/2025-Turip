package com.on.turip.feature.mypage.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.bookmark.api.BookmarkNavKey
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.mypage.api.MyPageNavKey
import com.on.turip.feature.mypage.impl.MyPageScreen
import com.on.turip.feature.trip.api.TripDetailNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class MyPageNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(MyPageNavKey::class, MyPageNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<MyPageNavKey> {
            MyPageScreen(
                onNavigateToAllBookmarkContents = { navigator.navigate(BookmarkNavKey) },
                onNavigateToContent = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
                onNavigateToInquiry = { },
                onNavigateToPrivacyPolicy = { },
                onNavigateToLogin = { navigator.goWithAllClear(LoginNavKey()) },
            )
        }
    }
}
