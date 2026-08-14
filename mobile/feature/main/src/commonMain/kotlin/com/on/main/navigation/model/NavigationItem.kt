package com.on.turip.feature.main.navigation.model

import androidx.compose.runtime.Immutable

@Immutable
data class NavigationItem(
    val label: String,
    val icon: NavigationIconModel,
)
