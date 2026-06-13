package com.on.turip

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import com.on.turip.core.domain.fid.DeviceFidManager
import com.on.turip.feature.main.MainApp
import com.on.turip.feature.main.navigation.SavedStateConfigurationProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    private val newDeepLinkFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val shouldLockPortrait = resources.configuration.smallestScreenWidthDp < 600
        requestedOrientation =
            if (shouldLockPortrait) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        val initialDeepLinkUrl = intent.deepLinkUrl()
        setContent {
            val savedStateConfigurationProvider: SavedStateConfigurationProvider = koinInject()
            val deviceFidManager: DeviceFidManager = koinInject()
            LaunchedEffect(Unit) {
                deviceFidManager.ensureInitialized()
            }
            MainApp(
                savedStateConfigurationProvider = savedStateConfigurationProvider,
                newDeepLinkFlow = newDeepLinkFlow,
                initialDeepLinkUrl = initialDeepLinkUrl,
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        intent.deepLinkUrl()?.let { newDeepLinkFlow.tryEmit(it) }
    }

    private fun Intent?.deepLinkUrl(): String? = this?.data?.toString()
}
