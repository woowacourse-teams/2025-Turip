package com.jetbrains.kmpapp.di

import org.koin.dsl.module

val useCaseModule =
    module {
    }

val coreModule =
    module {
        includes(useCaseModule)
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
