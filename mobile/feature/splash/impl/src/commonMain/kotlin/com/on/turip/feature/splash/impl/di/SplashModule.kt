package com.on.turip.feature.splash.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.splash.impl.SplashViewModel
import com.on.turip.feature.splash.impl.navigation.SplashNavKeyProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val splashModule = module {
    single { SplashNavKeyProvider() } bind NavKeyProvider::class
    viewModel {
        SplashViewModel(get(), get(), get())
    }
}
