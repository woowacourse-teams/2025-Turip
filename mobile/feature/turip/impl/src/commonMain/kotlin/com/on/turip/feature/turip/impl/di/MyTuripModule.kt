package com.on.turip.feature.turip.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.turip.impl.MyTuripViewModel
import com.on.turip.feature.turip.impl.navigation.MyTuripNavKeyProvider
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val myTuripModule = module {
    single { MyTuripNavKeyProvider() } bind NavKeyProvider::class
    viewModel<MyTuripViewModel> { MyTuripViewModel(get(), get(), get()) }
}
