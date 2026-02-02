package com.on.turip.ui.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.on.turip.R
import com.on.turip.ui.common.safeStartActivityWithToast
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.setting.SettingScreen
import com.on.turip.ui.compose.setting.model.InquiryMail
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
                    navigateToInquiry = { inquiryMail: InquiryMail ->
                        val uri =
                            "mailto:${InquiryMail.RECIPIENT}?subject=${Uri.encode(InquiryMail.TITLE)}&body=${
                                Uri.encode(inquiryMail.content)
                            }".toUri()

                        val intent: Intent = Intent(Intent.ACTION_SENDTO).apply { data = uri }
                        startActivity(intent)
                    },
                    navigateToPrivacyPolicy = { privacyPolicyLink: String ->
                        val intent = Intent(Intent.ACTION_VIEW, privacyPolicyLink.toUri())
                        safeStartActivityWithToast(
                            intent = intent,
                            errorToastMessage = getString(R.string.all_snackbar_not_found_privacy_policy_url),
                        )
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
