package com.on.turip.feature.turipdetail.impl.platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.on.turip.core.ui.model.turip.TuripShareModel
import com.on.turip.feature.turipdetail.impl.model.MapModel

@Composable
internal actual fun rememberTuripDetailPlatformActions(): TuripDetailPlatformActions {
    val context = LocalContext.current
    return remember(context) {
        TuripDetailPlatformActions(
            navigateToMap = { mapModel: MapModel ->
                context.safeStartActivityWithToast(
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapModel.uri)),
                    errorToastMessage = "지도 링크를 열 수 없어요",
                )
            },
            shareTuripByText = { turipShareModel: TuripShareModel ->
                context.safeStartActivityWithToast(
                    intent =
                        createShareChooserIntent(
                            text = turipShareModel.toShareFormat(),
                            title = turipShareModel.name,
                            baseIntentTitle = turipShareModel.name,
                        ),
                    errorToastMessage = "공유할 수 있는 앱을 찾을 수 없어요",
                )
            },
            shareTuripInvitationLink = { invitationLink: String ->
                context.safeStartActivityWithToast(
                    intent =
                        createShareChooserIntent(
                            text = invitationLink,
                            title = "초대 링크 공유",
                        ),
                    errorToastMessage = "공유할 수 있는 앱을 찾을 수 없어요",
                )
            },
        )
    }
}

private fun Context.safeStartActivityWithToast(
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

private fun createShareChooserIntent(
    text: String,
    title: String,
    baseIntentTitle: String? = null,
): Intent {
    val intent =
        createShareIntent(text = text).apply {
            baseIntentTitle?.let { putExtra(Intent.EXTRA_TITLE, it) }
        }

    val initialIntents =
        arrayOf(
            createShareIntent(text = text, packageName = KAKAO_PACKAGE),
            createShareIntent(text = text, packageName = INSTAGRAM_PACKAGE),
        )

    return Intent
        .createChooser(intent, title)
        .apply { putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents) }
}

private fun createShareIntent(
    text: String,
    packageName: String? = null,
): Intent =
    Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)

        packageName?.let { packageName ->
            `package` = packageName
        }
    }

private const val KAKAO_PACKAGE = "com.kakao.talk"
private const val INSTAGRAM_PACKAGE = "com.instagram.android"
