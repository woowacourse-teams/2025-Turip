package com.on.turip.feature.home.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.home.impl.navigation.HomeNavKeyProvider
import org.koin.dsl.bind
import org.koin.dsl.module

val homeModule = module {
    single { HomeNavKeyProvider() } bind NavKeyProvider::class
}
