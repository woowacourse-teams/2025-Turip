package com.on.turip.feature.bookmark.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.bookmark.api.BookmarkNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class BookmarkNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(BookmarkNavKey::class, BookmarkNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        bookmarkScreen(navigator)
    }
}
