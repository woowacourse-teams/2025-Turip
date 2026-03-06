package com.on.turip.ui.common.model.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class NavigationItem(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
)
