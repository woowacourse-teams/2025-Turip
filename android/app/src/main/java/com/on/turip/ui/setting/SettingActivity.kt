package com.on.turip.ui.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.on.turip.ui.compose.setting.LoginStatus
import com.on.turip.ui.compose.setting.SettingScreen
import com.on.turip.ui.compose.theme.TuripTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TuripTheme {
                SettingScreen(
                    loginStatus = LoginStatus.MEMBER,
                    onBackNavigate = { finish() },
                    onInquiryNavigate = { uri: Uri ->
                        val intent: Intent = Intent(Intent.ACTION_SENDTO).apply { data = uri }
                        startActivity(intent)
                    },
                    onPrivacyPolicyNavigate = { uri: Uri ->
                        val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    },
                    onLoginNavigate = {},
                    onLogoutNavigate = {},
                    onWithdrawClick = {},
                )
            }
        }
    }
}
