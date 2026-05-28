package com.on.turip.feature.bookmark.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.bookmark.impl.navigation.BookmarkNavKeyProvider
import com.on.turip.feature.bookmark.impl.viewmodel.BookmarkViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val bookmarkModule = module {
    viewModel { BookmarkViewModel(get(), get()) }
    single { BookmarkNavKeyProvider() } bind NavKeyProvider::class
}
