package com.on.turip.ui.main

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import com.on.turip.ui.compose.main.component.MainApp
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
        setContent {
            val isPhone = rememberIsPhone()
            requestedOrientation =
                if (isPhone) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            MainApp(savedStateConfigurationProvider)
        }
    }

    @Composable
    private fun rememberIsPhone(): Boolean {
        val configuration = LocalConfiguration.current
        return remember { configuration.smallestScreenWidthDp < 600 }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
