package com.on.turip.feature.turip.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.turip.impl.navigation.MyTuripNavKeyProvider
import org.koin.dsl.bind
import org.koin.dsl.module

val myTuripModule = module {
    single { MyTuripNavKeyProvider() } bind NavKeyProvider::class
}
