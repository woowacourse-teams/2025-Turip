package com.on.turip.feature.search.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.search.impl.keyword.SearchViewModel
import com.on.turip.feature.search.impl.navigation.SearchNavKeyProvider
import com.on.turip.feature.search.impl.regionresult.RegionResultViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val searchModule = module {
    single { SearchNavKeyProvider() } bind NavKeyProvider::class
    viewModel<SearchViewModel> { SearchViewModel(get(), get(), get()) }
    viewModel<RegionResultViewModel> { RegionResultViewModel(get(), get()) }
}
