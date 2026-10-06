package com.on.turip.feature.trip.impl.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.on.turip.core.model.trip.MapType
import com.on.turip.core.ui.model.turip.TuripShareModel
import com.on.turip.feature.trip.impl.model.MapModel
import com.swmansion.kmpsharing.SharingOptions
import com.swmansion.kmpsharing.rememberShare
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController

@Composable
internal actual fun rememberTripDetailPlatformActions(): TripDetailPlatformActions {
    val share = rememberShare()
    return remember(share) {
        TripDetailPlatformActions(
            navigateToMap = { mapModel: MapModel ->
                presentMapChooser(mapModel)
            },
            navigateToWebViewUrl = { url: String ->
                openUrl(url)
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

@OptIn(ExperimentalForeignApi::class)
private fun presentMapChooser(mapModel: MapModel) {
    val rootViewController = topViewController() ?: run {
        openUrl(mapModel.uri)
        return
    }

    // 이미 액션시트가 떠 있으면 중복으로 띄우지 않는다. (연속 탭 방지)
    if (rootViewController is UIAlertController) return

    val alert =
        UIAlertController.alertControllerWithTitle(
            title = null,
            message = null,
            preferredStyle = UIAlertControllerStyleActionSheet,
        )

    alert.addAction(
        UIAlertAction.actionWithTitle(
            title = APPLE_MAP_TITLE,
            style = UIAlertActionStyleDefault,
            handler = { openUrl(mapModel.appleMap.url) },
        ),
    )

    if (mapModel.uri.isNotBlank()) {
        alert.addAction(
            UIAlertAction.actionWithTitle(
                title = mapModel.type.thirdPartyTitle(),
                style = UIAlertActionStyleDefault,
                handler = { openUrl(mapModel.uri) },
            ),
        )
    }

    alert.addAction(
        UIAlertAction.actionWithTitle(
            title = CANCEL_TITLE,
            style = UIAlertActionStyleCancel,
            handler = null,
        ),
    )

    // iPad에서는 액션시트가 popover로 표시되므로 앵커를 지정하지 않으면 크래시가 발생한다.
    alert.popoverPresentationController?.apply {
        sourceView = rootViewController.view
        sourceRect = rootViewController.view.bounds
    }

    rootViewController.presentViewController(alert, animated = true, completion = null)
}

private fun MapType.thirdPartyTitle(): String =
    when (this) {
        MapType.KAKAO -> "카카오맵으로 보기"
        MapType.GOOGLE -> "구글 지도로 보기"
        MapType.NONE -> "지도 링크 열기"
    }

private fun topViewController(): UIViewController? {
    var current =
        UIApplication.sharedApplication
            .keyWindow
            ?.rootViewController
    while (current?.presentedViewController != null) {
        current = current.presentedViewController
    }
    return current
}

private const val APPLE_MAP_TITLE = "Apple 지도로 보기"
private const val CANCEL_TITLE = "취소"

private fun openUrl(url: String) {
    val normalizedUrl = url.normalizedExternalUrl()
    val nsUrl = NSURL.URLWithString(normalizedUrl) ?: run {
        Napier.e("iOS failed to create NSURL. url=$url")
        return
    }
    UIApplication.sharedApplication.openURL(
        url = nsUrl,
        options = emptyMap<Any?, Any?>(),
        completionHandler = { success ->
            if (!success) {
                Napier.e("iOS failed to open external URL. url=$normalizedUrl")
            }
        },
    )
}

private fun String.normalizedExternalUrl(): String {
    val trimmedUrl = trim()
    return if (trimmedUrl.contains("://")) trimmedUrl else "https://$trimmedUrl"
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
