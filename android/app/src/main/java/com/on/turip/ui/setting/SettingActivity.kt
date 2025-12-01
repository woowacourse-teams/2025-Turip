package com.on.turip.ui.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.on.turip.ui.compose.setting.SettingScreen
import com.on.turip.ui.compose.theme.TuripTheme
import com.on.turip.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TuripTheme {
                SettingScreen(
                    navigateToBack = { finish() },
                    navigateToInquiry = { uri: Uri ->
                        val intent: Intent = Intent(Intent.ACTION_SENDTO).apply { data = uri }
                        startActivity(intent)
                    },
                    navigateToPrivacyPolicy = { uri: Uri ->
                        val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    },
                    navigateToLoginScreen = {
                        val intent: Intent =
                            LoginActivity.newIntent(this).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        startActivity(intent)
                        finish()
                    },
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }
}
