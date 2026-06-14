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
    override suspend fun getIdToken(): TuripResult<String> =
        TuripResult.Failure(
            errorType = ErrorType.Cancelled,
            cause = UnsupportedOperationException("Apple login is only available on iOS."),
        )
}
