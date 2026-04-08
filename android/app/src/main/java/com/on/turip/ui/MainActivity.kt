package com.on.turip.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.on.turip.ui.compose.main.MainApp
import com.on.turip.ui.compose.main.navigation.SavedStateConfigurationProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var savedStateConfigurationProvider: SavedStateConfigurationProvider

    private val _newDeepLink = Channel<String>(Channel.CONFLATED)
    val newDeepLink: Flow<String> = _newDeepLink.receiveAsFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val shouldLockPortrait = resources.configuration.smallestScreenWidthDp < 600
        requestedOrientation =
            if (shouldLockPortrait) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        setContent {
            MainApp(
                savedStateConfigurationProvider = savedStateConfigurationProvider,
                newDeepLinkFlow = newDeepLink,
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.dataString?.let { _newDeepLink.trySend(it) }
    }
}
