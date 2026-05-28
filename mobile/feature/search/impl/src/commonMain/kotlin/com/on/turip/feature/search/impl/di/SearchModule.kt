package com.on.turip.feature.search.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.search.impl.navigation.SearchNavKeyProvider
import org.koin.dsl.bind
import org.koin.dsl.module

val searchModule = module {
    single { SearchNavKeyProvider() } bind NavKeyProvider::class
}
