package com.on.turip

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.jetbrains.kmpapp.di.initLogger
import com.on.turip.di.initKoin
import com.on.turip.feature.main.MainApp
import com.on.turip.feature.main.navigation.SavedStateConfigurationProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.compose.koinInject

fun MainViewController() = ComposeUIViewController(
    configure = {
        initLogger()
        initKoin()
    }
) {
    val savedStateConfigurationProvider: SavedStateConfigurationProvider = koinInject()
    val newDeepLinkFlow = remember { MutableSharedFlow<String>(extraBufferCapacity = 1) }
    MainApp(
        savedStateConfigurationProvider = savedStateConfigurationProvider,
        newDeepLinkFlow = newDeepLinkFlow,
    )
}
