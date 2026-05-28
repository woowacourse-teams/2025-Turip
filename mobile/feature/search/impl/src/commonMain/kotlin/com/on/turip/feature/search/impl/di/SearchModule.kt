package com.on.turip.feature.search.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.search.impl.navigation.SearchNavKeyProvider
import com.on.turip.feature.search.impl.viewmodel.RegionResultViewModel
import com.on.turip.feature.search.impl.viewmodel.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val searchModule = module {
    viewModel { params -> SearchViewModel(params.get(), get(), get()) }
    viewModel { params -> RegionResultViewModel(params.get(), get()) }
    single { SearchNavKeyProvider() } bind NavKeyProvider::class
}
