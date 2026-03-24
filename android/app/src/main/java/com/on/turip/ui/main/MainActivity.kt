package com.on.turip.ui.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.on.turip.ui.compose.main.MainApp
import com.on.turip.ui.compose.main.navigation.SavedStateConfigurationProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var savedStateConfigurationProvider: SavedStateConfigurationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val shouldLockPortrait = resources.configuration.smallestScreenWidthDp < 600
        requestedOrientation =
            if (shouldLockPortrait) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        setContent { MainApp(savedStateConfigurationProvider = savedStateConfigurationProvider) }
    }
}
