package com.on.main.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.main.navigation.SavedStateConfigurationProvider
import org.koin.dsl.module

val mainModule = module {
    single {
        SavedStateConfigurationProvider(
            providers = getAll<NavKeyProvider>(),
        )
    }
}
