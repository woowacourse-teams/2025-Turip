package com.on.turip

import androidx.compose.ui.window.ComposeUIViewController
import com.jetbrains.kmpapp.di.initLogger
import com.on.turip.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initLogger()
        initKoin()
    }
) {
    App()
}
