package com.on.turip.feature.login.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.login.impl.LoginUseCase
import com.on.turip.feature.login.impl.LoginViewModel
import com.on.turip.feature.login.impl.navigation.LoginNavKeyProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val loginModule = module {
    single { LoginNavKeyProvider() } bind NavKeyProvider::class
    single { LoginUseCase(get(), get(), get(), get()) }
    viewModel {
        LoginViewModel(get(), get(), get(), get())
    }
}
