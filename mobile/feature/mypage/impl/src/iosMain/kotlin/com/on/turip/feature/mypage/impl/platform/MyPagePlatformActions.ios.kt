package com.on.turip.feature.mypage.impl.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.on.turip.feature.mypage.impl.model.InquiryMail
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard
import platform.UIKit.UIViewController

@Composable
internal actual fun rememberMyPagePlatformActions(): MyPagePlatformActions =
    remember {
        MyPagePlatformActions(
            openInquiryMail = { mail: InquiryMail ->
                openMail(mail)
            },
            openPrivacyPolicyUrl = { url: String ->
                openUrl(url)
            },
        )
    }

private fun openMail(mail: InquiryMail) {
    val mailtoUrl =
        "mailto:${InquiryMail.RECIPIENT}" +
            "?subject=${encodeMailQueryValue(InquiryMail.TITLE)}" +
            "&body=${encodeMailQueryValue(mail.content)}"
    openUrl(
        url = mailtoUrl,
        onFailure = {
            shareText(
                """
                To: ${InquiryMail.RECIPIENT}
                Subject: ${InquiryMail.TITLE}
                
                ${mail.content}
                """.trimIndent(),
            )
        },
    )
}

private fun openUrl(
    url: String,
    onFailure: () -> Unit = {},
) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(
        url = nsUrl,
        options = emptyMap<Any?, Any?>(),
        completionHandler = { success ->
            if (!success) onFailure()
        },
    )
}

private fun shareText(text: String) {
    val rootViewController = topViewController() ?: run {
        UIPasteboard.generalPasteboard.string = text
        return
    }
    val activityViewController =
        UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null,
        )
    rootViewController.presentViewController(activityViewController, animated = true, completion = null)
}

private fun topViewController(): UIViewController? {
    val rootViewController =
        UIApplication.sharedApplication
            .keyWindow
            ?.rootViewController

    var current = rootViewController
    while (current?.presentedViewController != null) {
        current = current.presentedViewController
    }
    return current
}
