package com.on.turip.feature.splash.impl.di

import com.on.turip.core.domain.session.usecase.DetermineInitialSessionUseCase
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.splash.impl.navigation.SplashNavKeyProvider
import com.on.turip.feature.splash.impl.viewmodel.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val splashModule = module {
    single { DetermineInitialSessionUseCase(get(), get()) }
    viewModel { SplashViewModel(get(), get(), get(), get()) }
    single { SplashNavKeyProvider() } bind NavKeyProvider::class
}
