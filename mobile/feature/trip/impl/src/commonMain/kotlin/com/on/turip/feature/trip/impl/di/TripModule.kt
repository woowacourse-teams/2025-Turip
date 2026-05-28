package com.on.turip.feature.trip.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.trip.impl.navigation.TripNavKeyProvider
import org.koin.dsl.bind
import org.koin.dsl.module

val tripModule = module {
    single { TripNavKeyProvider() } bind NavKeyProvider::class
}
