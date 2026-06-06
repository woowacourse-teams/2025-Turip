package com.on.turip.core.network.di

import com.on.turip.core.network.datasourceimpl.DefaultBookmarkRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultTuripRemoteDataSource
import org.koin.dsl.module

fun datasourceModule(baseUrl: String) = module {
    single {
        DefaultBookmarkRemoteDataSource(bookmarkService = get())
        DefaultTuripRemoteDataSource(turipService = get())

    }
}
