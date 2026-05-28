package com.on.turip.feature.turipdetail.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.turipdetail.impl.navigation.TuripDetailNavKeyProvider
import com.on.turip.feature.turipdetail.impl.viewmodel.TuripDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val turipDetailModule = module {
    viewModel { params -> TuripDetailViewModel(params.get(), get()) }
    single { TuripDetailNavKeyProvider() } bind NavKeyProvider::class
}
