package com.on.turip.feature.regionbriefing.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.regionbriefing.impl.RegionBriefingViewModel
import com.on.turip.feature.regionbriefing.impl.navigation.RegionBriefingNavKeyProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val regionBriefingModule = module {
    single { RegionBriefingNavKeyProvider() } bind NavKeyProvider::class
    viewModel<RegionBriefingViewModel> { RegionBriefingViewModel(get(), get(), get()) }
}
