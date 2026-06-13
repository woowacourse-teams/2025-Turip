package com.on.turip

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import com.jetbrains.kmpapp.di.initLogger
import com.on.turip.core.domain.fid.DeviceFidManager
import com.on.turip.core.ui.platform.IosGoogleMapBridge
import com.on.turip.core.ui.platform.IosGoogleMapViewFactory
import com.on.turip.core.ui.platform.IosGoogleMapViewUpdater
import com.on.turip.feature.main.MainApp
import com.on.turip.feature.main.navigation.SavedStateConfigurationProvider
import org.koin.compose.koinInject

fun MainViewController() = ComposeUIViewController(
    configure = {
        initLogger()
    }
) {
    val savedStateConfigurationProvider: SavedStateConfigurationProvider = koinInject()
    val deviceFidManager: DeviceFidManager = koinInject()
    LaunchedEffect(Unit) {
        deviceFidManager.ensureInitialized()
    }
    MainApp(
        savedStateConfigurationProvider = savedStateConfigurationProvider,
        newDeepLinkFlow = iosDeepLinkFlow(),
        initialDeepLinkUrl = null,
    )
}

fun registerIosGoogleMapView(
    factory: IosGoogleMapViewFactory,
    updater: IosGoogleMapViewUpdater,
) {
    IosGoogleMapBridge.register(factory, updater)
}

fun googleMapsApiKey(): String = BuildKonfig.GOOGLE_MAPS_API_KEY
