package com.on.turip.ui.compose.turipdetail.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class TuripDetailNavKey(
    val turipId: Long,
) : NavKey
