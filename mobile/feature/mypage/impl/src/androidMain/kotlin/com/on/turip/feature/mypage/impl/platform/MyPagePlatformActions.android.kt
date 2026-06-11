package com.on.turip.feature.mypage.impl.platform

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.on.turip.feature.mypage.impl.model.InquiryMail

@Composable
internal actual fun rememberMyPagePlatformActions(): MyPagePlatformActions {
    val context = LocalContext.current
    return remember(context) {
        MyPagePlatformActions(
            openInquiryMail = { mail: InquiryMail ->
                val intent =
                    Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${InquiryMail.RECIPIENT}")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(InquiryMail.RECIPIENT))
                        putExtra(Intent.EXTRA_SUBJECT, InquiryMail.TITLE)
                        putExtra(Intent.EXTRA_TEXT, mail.content)
                    }
                context.safeStartActivityWithToast(
                    intent = intent,
                    errorToastMessage = "메일 앱을 열 수 없어요",
                )
            },
            openPrivacyPolicyUrl = { url: String ->
                context.safeStartActivityWithToast(
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)),
                    errorToastMessage = "링크를 열 수 없어요",
                )
            },
        )
    }
}

private fun android.content.Context.safeStartActivityWithToast(
    intent: Intent,
    errorToastMessage: String,
) {
    runCatching { startActivity(intent) }
        .onFailure { throwable ->
            if (throwable is ActivityNotFoundException) {
                Toast.makeText(this, errorToastMessage, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, errorToastMessage, Toast.LENGTH_SHORT).show()
            }
        }
}
