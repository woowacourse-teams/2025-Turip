package com.on.turip.feature.main.component

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat

@Composable
actual fun SystemBarStyleEffect(style: SystemBarStyle) {
    val activity = LocalActivity.current
    SideEffect {
        val window = activity?.window ?: return@SideEffect
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = style.isLightStatusBarIcons
        controller.isAppearanceLightNavigationBars = style.isLightNavigationBarIcons
    }
}
