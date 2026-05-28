package com.on.turip.feature.turipdetail.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.turipdetail.impl.navigation.TuripDetailNavKeyProvider
import org.koin.dsl.bind
import org.koin.dsl.module

val turipDetailModule = module {
    single { TuripDetailNavKeyProvider() } bind NavKeyProvider::class
}
