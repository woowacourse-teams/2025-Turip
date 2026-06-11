package com.on.turip.feature.turipdetail.impl.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.on.turip.core.ui.model.turip.TuripShareModel
import com.on.turip.feature.turipdetail.impl.model.MapModel
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard
import platform.UIKit.UIViewController

@Composable
internal actual fun rememberTuripDetailPlatformActions(): TuripDetailPlatformActions =
    remember {
        TuripDetailPlatformActions(
            navigateToMap = { mapModel: MapModel ->
                openUrl(mapModel.uri)
            },
            shareTuripByText = { turipShareModel: TuripShareModel ->
                shareText(turipShareModel.toShareFormat())
            },
            shareTuripInvitationLink = { invitationLink: String ->
                shareText(invitationLink)
            },
        )
    }

private fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(nsUrl)
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
