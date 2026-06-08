package com.on.turip

import com.on.turip.core.local.di.localModuleIos
import com.on.turip.di.initKoin

fun startKoinIos() {
    initKoin {
        modules(localModuleIos)
    }
}
