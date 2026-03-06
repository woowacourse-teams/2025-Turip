package com.on.turip.ui.compose.main.navigation.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class NavigationItem(
    @StringRes val labelRes: Int,
    val icon: NavigationIconModel,
)
