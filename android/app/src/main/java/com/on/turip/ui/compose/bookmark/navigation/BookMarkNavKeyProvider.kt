package com.on.turip.ui.compose.bookmark.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import jakarta.inject.Inject
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class BookMarkNavKeyProvider @Inject constructor() : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(BookmarkNavKey::class, BookmarkNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        bookmarkContentListScreen(navigator)
    }
}
