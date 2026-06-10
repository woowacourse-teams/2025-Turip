package com.on.turip.feature.login.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult

@Composable
internal actual fun rememberGoogleCredentialManager(): GoogleCredentialManager =
    remember {
        object : GoogleCredentialManager {
            override suspend fun getIdToken(): TuripResult<String> =
                TuripResult.Failure(
                    errorType = ErrorType.Unknown,
                    cause = UnsupportedOperationException("Google login is not implemented on iOS"),
                )
        }
    }
