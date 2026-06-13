package com.on.turip.feature.turipdetail.impl.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.on.turip.core.ui.model.turip.TuripShareModel
import com.on.turip.feature.turipdetail.impl.model.MapModel
import com.swmansion.kmpsharing.SharingOptions
import com.swmansion.kmpsharing.rememberShare
import io.github.aakira.napier.Napier
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
internal actual fun rememberTuripDetailPlatformActions(): TuripDetailPlatformActions {
    val share = rememberShare()
    return remember(share) {
        TuripDetailPlatformActions(
            navigateToMap = { mapModel: MapModel ->
                openUrl(mapModel.uri)
            },
            shareTuripByText = { turipShareModel: TuripShareModel ->
                shareText(
                    share = share,
                    text = turipShareModel.toShareFormat(),
                )
            },
            shareTuripInvitationLink = { invitationLink: String ->
                shareText(
                    share = share,
                    text = invitationLink.toInvitationShareText(),
                )
            },
        )
    }
}

private fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(nsUrl)
}

private fun String.toInvitationShareText(): String =
    "튜립 초대 링크\n$this"

private fun shareText(
    share: com.swmansion.kmpsharing.Share,
    text: String,
) {
    Napier.d("iOS kmp-sharing requested. textLength=${text.length}")
    runCatching {
        share(
            data = text,
            options = iosTextSharingOptions(),
        )
    }.onFailure { throwable ->
        Napier.e("iOS kmp-sharing failed", throwable)
    }
}

private fun iosTextSharingOptions(): SharingOptions =
    SharingOptions(
        iosUTI = TEXT_PLAIN_UTI,
    )

private const val TEXT_PLAIN_UTI = "public.plain-text"
