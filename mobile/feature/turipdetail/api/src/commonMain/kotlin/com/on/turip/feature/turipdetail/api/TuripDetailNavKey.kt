package com.on.turip.feature.turipdetail.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class TuripDetailNavKey(
    val turipId: Long,
) : NavKey
