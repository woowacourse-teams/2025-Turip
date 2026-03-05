package com.on.turip.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.splash.SplashScreen
import com.on.turip.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TuripTheme {
                SplashScreen(
                    navigateToHome = {
                        startActivity(MainActivity.newIntent(this@SplashActivity))
                        finish()
                    },
                    navigateToLogin = {
                        startActivity(MainActivity.newIntent(this@SplashActivity))
                        finish()
                    },
                    onFinish = ::finish,
                )
            }
        }
    }
}
