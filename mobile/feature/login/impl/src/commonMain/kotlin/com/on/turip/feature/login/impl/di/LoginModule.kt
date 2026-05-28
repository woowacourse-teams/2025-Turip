package com.on.turip.feature.login.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.login.impl.navigation.LoginNavKeyProvider
import org.koin.dsl.bind
import org.koin.dsl.module

val loginModule = module {
    single { LoginNavKeyProvider() } bind NavKeyProvider::class
}
