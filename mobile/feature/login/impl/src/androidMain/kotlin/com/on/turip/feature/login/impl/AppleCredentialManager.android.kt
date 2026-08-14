package com.on.turip.feature.login.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult

@Composable
internal actual fun rememberAppleCredentialManager(): AppleCredentialManager =
    remember {
        AndroidAppleCredentialManager()
    }

private class AndroidAppleCredentialManager : AppleCredentialManager {
    override suspend fun getCredential(): TuripResult<AppleCredential> =
        TuripResult.Failure(
            errorType = ErrorType.Unknown,
            cause = UnsupportedOperationException("Apple login is only available on iOS."),
        )
}
