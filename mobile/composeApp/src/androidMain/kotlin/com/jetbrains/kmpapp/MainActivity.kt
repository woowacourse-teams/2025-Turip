package com.on.turip

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
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
        handleDeepLink(intent)
        setContent {
            val isPhone = rememberIsPhone()
            requestedOrientation =
                if (isPhone) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            val savedStateConfigurationProvider: SavedStateConfigurationProvider = koinInject()
            val deviceFidManager: DeviceFidManager = koinInject()
            LaunchedEffect(Unit) {
                deviceFidManager.ensureInitialized()
            }
            MainApp(
                savedStateConfigurationProvider = savedStateConfigurationProvider,
                newDeepLinkFlow = newDeepLinkFlow,
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.toString()?.let { newDeepLinkFlow.tryEmit(it) }
    }

    @Composable
    private fun rememberIsPhone(): Boolean {
        val configuration = LocalConfiguration.current
        return remember { configuration.smallestScreenWidthDp < 600 }
    }
}
