package com.on.turip.feature.bookmark.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.bookmark.api.BookmarkNavKey
import com.on.turip.feature.bookmark.impl.BookmarkContentListScreen
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.trip.api.TripDetailNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class BookmarkNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(BookmarkNavKey::class, BookmarkNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<BookmarkNavKey> {
            BookmarkContentListScreen(
                onBack = navigator::goBack,
                onNavigateToLogin = { navigator.goWithAllClear(LoginNavKey()) },
                onNavigateToContent = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
            )
        }
    }
}
