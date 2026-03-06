package com.on.turip.ui.common.model.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class NavigationItem(
    @StringRes val labelRes: Int,
    val icon: NavigationIconModel,
)
