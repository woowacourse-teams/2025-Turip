package com.on.turip.core.network.di

import com.on.turip.core.data.datasource.BookmarkRemoteDataSource
import com.on.turip.core.data.datasource.TuripRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultBookmarkRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultTuripRemoteDataSource
import org.koin.dsl.module

fun datasourceModule(baseUrl: String) = module {
    single<BookmarkRemoteDataSource> { DefaultBookmarkRemoteDataSource(bookmarkService = get()) }
    single<TuripRemoteDataSource> { DefaultTuripRemoteDataSource(turipService = get()) }
}
