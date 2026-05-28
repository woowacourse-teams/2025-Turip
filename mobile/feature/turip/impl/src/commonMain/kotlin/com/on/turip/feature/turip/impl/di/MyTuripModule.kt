package com.on.turip.feature.turip.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.turip.impl.navigation.MyTuripNavKeyProvider
import com.on.turip.feature.turip.impl.viewmodel.MyTuripViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val myTuripModule = module {
    viewModel { MyTuripViewModel(get()) }
    single { MyTuripNavKeyProvider() } bind NavKeyProvider::class
}
