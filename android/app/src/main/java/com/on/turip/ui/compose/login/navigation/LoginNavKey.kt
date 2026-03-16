package com.on.turip.ui.compose.login.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class LoginNavKey(
    val deepLinkUrl: String? = null,
) : NavKey
