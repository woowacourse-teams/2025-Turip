package com.on.turip.ui.compose.main.component

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class SystemBarStyle(
    val isLightStatusBarIcons: Boolean = true,
    val isLightNavigationBarIcons: Boolean = true,
)

@Stable
class SystemBarStyleController {
    var style: SystemBarStyle by mutableStateOf(SystemBarStyle())
        private set

    fun update(style: SystemBarStyle) {
        this.style = style
    }

    fun reset() {
        style = SystemBarStyle()
    }
}

val LocalSystemBarStyleController =
    staticCompositionLocalOf<SystemBarStyleController> {
        error("SystemBarStyleController is not provided")
    }
