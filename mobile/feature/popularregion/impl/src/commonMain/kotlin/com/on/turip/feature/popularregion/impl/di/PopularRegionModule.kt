package com.on.turip.feature.popularregion.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.popularregion.impl.PopularRegionViewModel
import com.on.turip.feature.popularregion.impl.navigation.PopularRegionNavKeyProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val popularRegionModule = module {
    single { PopularRegionNavKeyProvider() } bind NavKeyProvider::class
    viewModel<PopularRegionViewModel> { PopularRegionViewModel(get(), get(), get()) }
}
