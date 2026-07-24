package com.on.turip.feature.bookmark.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.bookmark.impl.BookmarkContentListViewModel
import com.on.turip.feature.bookmark.impl.navigation.BookmarkNavKeyProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val bookmarkModule = module {
    single { BookmarkNavKeyProvider() } bind NavKeyProvider::class
    viewModel<BookmarkContentListViewModel> {
        BookmarkContentListViewModel(
            sessionManager = get(),
            bookmarkRepository = get(),
        )
    }
}
