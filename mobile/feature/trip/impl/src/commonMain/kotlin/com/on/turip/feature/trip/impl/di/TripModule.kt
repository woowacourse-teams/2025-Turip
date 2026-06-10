package com.on.turip.feature.trip.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.trip.impl.TripDetailViewModel
import com.on.turip.feature.trip.impl.navigation.TripNavKeyProvider
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val tripModule = module {
    single { TripNavKeyProvider() } bind NavKeyProvider::class
    viewModel<TripDetailViewModel> { TripDetailViewModel(get(), get(), get(), get()) }
}
