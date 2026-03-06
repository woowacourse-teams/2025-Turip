package com.on.turip.ui.common.model.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class NavigationItem(
    val label: String,
    val icon: ImageVector,
)
