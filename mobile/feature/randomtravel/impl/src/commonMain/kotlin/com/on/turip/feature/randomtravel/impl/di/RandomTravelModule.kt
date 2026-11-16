package com.on.turip.feature.randomtravel.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.randomtravel.impl.RandomTravelViewModel
import com.on.turip.feature.randomtravel.impl.navigation.RandomTravelNavKeyProvider
import com.on.turip.feature.randomtravel.impl.relatedspot.RelatedSpotDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val randomTravelModule = module {
    single { RandomTravelNavKeyProvider() } bind NavKeyProvider::class
    viewModel<RandomTravelViewModel> { RandomTravelViewModel(get(), get(), get(), get()) }
    viewModel<RelatedSpotDetailViewModel> { RelatedSpotDetailViewModel(get(), get()) }
}
