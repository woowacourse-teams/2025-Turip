package com.on.turip.feature.mypage.impl.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.on.turip.feature.mypage.impl.model.InquiryMail
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
internal actual fun rememberMyPagePlatformActions(): MyPagePlatformActions =
    remember {
        MyPagePlatformActions(
            openInquiryMail = { mail: InquiryMail ->
                openUrl(
                    "mailto:${InquiryMail.RECIPIENT}" +
                        "?subject=${encodeMailQueryValue(InquiryMail.TITLE)}" +
                        "&body=${encodeMailQueryValue(mail.content)}",
                )
            },
            openPrivacyPolicyUrl = { url: String ->
                openUrl(url)
            },
        )
    }

private fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(nsUrl)
}
