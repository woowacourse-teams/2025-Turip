package com.on.turip.feature.login.impl

import androidx.compose.runtime.Composable
import com.on.turip.core.model.result.TuripResult

interface AppleCredentialManager {
    suspend fun getIdToken(): TuripResult<String>
}

@Composable
internal expect fun rememberAppleCredentialManager(): AppleCredentialManager
