package com.on.turip.feature.trip.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.trip.impl.TripDetailViewModel
import com.on.turip.feature.trip.impl.navigation.TripNavKeyProvider
import com.on.turip.feature.trip.impl.turipselection.PlaceTuripSelectionViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

fun tripModule(webViewBaseUrl: String) = module {
    single { TripNavKeyProvider(webViewBaseUrl = webViewBaseUrl) } bind NavKeyProvider::class
    viewModel<TripDetailViewModel> { TripDetailViewModel(get(), get(), get(), get()) }
    viewModel<PlaceTuripSelectionViewModel> { PlaceTuripSelectionViewModel(get(), get()) }
}
