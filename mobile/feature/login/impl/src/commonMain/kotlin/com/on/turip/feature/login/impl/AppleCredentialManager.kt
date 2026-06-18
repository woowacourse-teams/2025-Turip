package com.on.turip.feature.login.impl

import androidx.compose.runtime.Composable
import com.on.turip.core.model.result.TuripResult

data class AppleCredential(
    val idToken: String,
    val rawNonce: String,
)

interface AppleCredentialManager {
    suspend fun getCredential(): TuripResult<AppleCredential>
}

@Composable
internal expect fun rememberAppleCredentialManager(): AppleCredentialManager
