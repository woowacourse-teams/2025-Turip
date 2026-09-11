package com.on.turip.feature.randomtravel.impl.platform

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberReduceMotionEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                DEFAULT_ANIMATOR_DURATION_SCALE,
            )
        }.getOrDefault(DEFAULT_ANIMATOR_DURATION_SCALE) == 0f
    }
}

private const val DEFAULT_ANIMATOR_DURATION_SCALE: Float = 1f
