package com.on.turip.feature.mypage.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.bookmark.api.BookmarkNavKey
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.mypage.api.MyPageNavKey
import com.on.turip.feature.mypage.api.NotificationSettingNavKey
import com.on.turip.feature.mypage.impl.MyPageScreen
import com.on.turip.feature.mypage.impl.notificationsetting.NotificationSettingScreen
import com.on.turip.feature.mypage.impl.platform.rememberMyPagePlatformActions
import com.on.turip.feature.trip.api.TripDetailNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class MyPageNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(MyPageNavKey::class, MyPageNavKey.serializer())
        subclass(NotificationSettingNavKey::class, NotificationSettingNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<MyPageNavKey> {
            val platformActions = rememberMyPagePlatformActions()
            MyPageScreen(
                onNavigateToAllBookmarkContents = { navigator.navigate(BookmarkNavKey) },
                onNavigateToContent = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
                onNavigateToInquiry = platformActions.openInquiryMail,
                onNavigateToPrivacyPolicy = platformActions.openPrivacyPolicyUrl,
                onNavigateToLogin = { navigator.goWithAllClear(LoginNavKey()) },
                onNavigateToNotificationSetting = { navigator.navigate(NotificationSettingNavKey) },
            )
        }

        entry<NotificationSettingNavKey> {
            NotificationSettingScreen(onBackClick = navigator::goBack)
        }
    }
}
