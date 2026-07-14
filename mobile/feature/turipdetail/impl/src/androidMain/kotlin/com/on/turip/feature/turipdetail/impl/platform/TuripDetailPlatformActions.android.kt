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
import com.swmansion.kmpsharing.SharingOptions
import com.swmansion.kmpsharing.rememberShare

@Composable
internal actual fun rememberTuripDetailPlatformActions(): TuripDetailPlatformActions {
    val context = LocalContext.current
    val share = rememberShare()
    return remember(context, share) {
        TuripDetailPlatformActions(
            navigateToMap = { mapModel: MapModel ->
                context.safeStartActivityWithToast(
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapModel.uri)),
                    errorToastMessage = "지도 링크를 열 수 없어요",
                )
            },
            shareTuripByText = { turipShareModel: TuripShareModel ->
                share(
                    data = turipShareModel.toShareFormat(),
                    options =
                        SharingOptions(
                            androidDialogTitle = turipShareModel.name,
                            androidMimeType = TEXT_PLAIN_MIME_TYPE,
                        ),
                )
            },
            shareTuripInvitationLink = { invitationLink: String ->
                share(
                    data = invitationLink,
                    options =
                        SharingOptions(
                            androidDialogTitle = "초대 링크 공유",
                            androidMimeType = TEXT_PLAIN_MIME_TYPE,
                        ),
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

private const val TEXT_PLAIN_MIME_TYPE = "text/plain"
