package com.on.turip.feature.randomtravel.impl.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.aakira.napier.Napier
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
internal actual fun rememberRelatedSpotDetailPlatformActions(): RelatedSpotDetailPlatformActions =
    remember {
        RelatedSpotDetailPlatformActions(
            openKakaoMapUrl = { url: String ->
                openUrl(url)
            },
        )
    }

private fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: run {
        Napier.e("iOS failed to create NSURL. url=$url")
        return
    }
    UIApplication.sharedApplication.openURL(
        url = nsUrl,
        options = emptyMap<Any?, Any?>(),
        completionHandler = { success ->
            if (!success) {
                Napier.e("iOS failed to open external URL. url=$url")
            }
        },
    )
}
