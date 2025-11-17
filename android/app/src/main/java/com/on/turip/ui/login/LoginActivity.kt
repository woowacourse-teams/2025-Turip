package com.on.turip.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.on.turip.ui.compose.login.LoginScreen
import com.on.turip.ui.compose.theme.TuripTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            ComposeView(this).apply {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
                )
                setContent {
                    TuripTheme {
                        LoginScreen()
                    }
                }
            },
        )
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }
}
