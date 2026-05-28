package com.on.turip.feature.home.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.home.impl.navigation.HomeNavKeyProvider
import com.on.turip.feature.home.impl.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
    single { HomeNavKeyProvider() } bind NavKeyProvider::class
}
