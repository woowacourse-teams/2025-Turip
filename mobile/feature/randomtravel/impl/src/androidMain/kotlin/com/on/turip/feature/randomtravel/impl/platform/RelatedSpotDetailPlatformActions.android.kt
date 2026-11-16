package com.on.turip.feature.randomtravel.impl.platform

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
internal actual fun rememberRelatedSpotDetailPlatformActions(): RelatedSpotDetailPlatformActions {
    val context = LocalContext.current
    return remember(context) {
        RelatedSpotDetailPlatformActions(
            openKakaoMapUrl = { url: String ->
                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }
                    .onFailure { throwable ->
                        if (throwable is ActivityNotFoundException) {
                            Toast.makeText(context, "카카오맵을 열 수 없어요", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "카카오맵을 열 수 없어요", Toast.LENGTH_SHORT).show()
                        }
                    }
            },
        )
    }
}
