package com.on.turip.feature.login.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class LoginNavKey(
    val deepLinkUrl: String? = null,
) : NavKey
