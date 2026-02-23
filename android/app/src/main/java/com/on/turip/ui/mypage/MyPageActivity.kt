package com.on.turip.ui.mypage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.on.turip.R
import com.on.turip.ui.bookmarks.BookmarkContentActivity
import com.on.turip.ui.common.extensions.safeStartActivityWithToast
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.mypage.MyPageScreen
import com.on.turip.ui.compose.mypage.model.InquiryMail
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.trip.TripDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TuripTheme {
                MyPageScreen(
                    navigateToAllBookmarkContents = {
                        val intent = BookmarkContentActivity.newIntent(this)
                        startActivity(intent)
                    },
                    navigateToContent = { contentId: Long ->
                        Timber.d("마이페이지 북마크 콘텐츠 클릭(contentId=$contentId)")
                        val intent: Intent =
                            TripDetailActivity.newIntent(context = this, contentId = contentId)
                        startActivity(intent)
                    },
                    navigateToInquiry = { mail: InquiryMail ->
                        val uri =
                            "mailto:${InquiryMail.RECIPIENT}?subject=${Uri.encode(InquiryMail.TITLE)}&body=${
                                Uri.encode(mail.content)
                            }".toUri()

                        val intent: Intent = Intent(Intent.ACTION_SENDTO).apply { data = uri }
                        startActivity(intent)
                    },
                    navigateToPrivacyPolicy = { url: String ->
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        safeStartActivityWithToast(
                            intent = intent,
                            errorToastMessage = getString(R.string.all_snackbar_not_found_privacy_policy_url),
                        )
                    },
                    navigateToLogin = {
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
        fun newIntent(context: Context): Intent = Intent(context, MyPageActivity::class.java)
    }
}
