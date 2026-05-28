package com.jetbrains.kmpapp.di

import com.on.turip.core.data.di.dataModule
import com.on.turip.core.network.di.datasourceModule
import com.on.turip.core.network.di.serviceModule
import com.on.turip.feature.main.navigation.SavedStateConfigurationProvider
import org.koin.dsl.module

val coreModule =
    module {
        includes(dataModule, serviceModule, datasourceModule)
    }

val featureModule =
    module {
        includes(
        )
    }

val appModule =
    module {
        includes(coreModule, featureModule)
        single { SavedStateConfigurationProvider(getAll()) }
    }
