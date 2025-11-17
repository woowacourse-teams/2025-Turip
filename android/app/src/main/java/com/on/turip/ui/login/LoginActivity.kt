package com.on.turip.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.on.turip.ui.compose.login.LoginScreen
import com.on.turip.ui.compose.theme.TuripTheme
import com.on.turip.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            TuripTheme {
                LoginScreen(
                    navigateToMain = { startActivity(MainActivity.newIntent(this@LoginActivity)) },
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }
}
