package com.on.turip.feature.login.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.login.impl.navigation.LoginNavKeyProvider
import com.on.turip.feature.login.impl.viewmodel.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val loginModule = module {
    viewModel { (deepLinkUrl: String?) ->
        LoginViewModel(
            deepLinkUrl = deepLinkUrl,
            authRepository = get(),
            sessionManager = get(),
        )
    }
    single { LoginNavKeyProvider() } bind NavKeyProvider::class
}
