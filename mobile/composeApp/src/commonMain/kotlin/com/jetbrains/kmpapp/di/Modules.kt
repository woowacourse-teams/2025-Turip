package com.jetbrains.kmpapp.di

import com.on.turip.core.data.di.dataModule
import org.koin.dsl.module

val coreModule =
    module {
        includes(dataModule)
    }

val featureModule =
    module {
        includes(
        )
    }

val appModule =
    module {
        includes(coreModule, featureModule)
    }
