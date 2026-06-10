package com.on.turip.feature.turipdetail.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.turipdetail.impl.TuripDetailViewModel
import com.on.turip.feature.turipdetail.impl.navigation.TuripDetailNavKeyProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val turipDetailModule = module {
    single { TuripDetailNavKeyProvider() } bind NavKeyProvider::class
    viewModel<TuripDetailViewModel> { TuripDetailViewModel(get(), get(), get(), get()) }
}
