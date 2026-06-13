package com.on.turip.feature.splash.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class SplashNavKey(
    val deepLinkUrl: String? = null,
) : NavKey
