package com.on.turip.feature.trip.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.trip.impl.navigation.TripDetailNavKeyProvider
import com.on.turip.feature.trip.impl.viewmodel.TripDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val tripModule = module {
    viewModel { params -> TripDetailViewModel(params.get(), get(), get()) }
    single { TripDetailNavKeyProvider() } bind NavKeyProvider::class
}
