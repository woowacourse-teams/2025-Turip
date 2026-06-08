package com.on.turip.core.local.fid

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull

private const val FID_WAIT_TIMEOUT_MILLIS = 3_000L

private val iosFirebaseFid = CompletableDeferred<String?>()

fun provideIosFirebaseInstallationId(fid: String?) {
    if (!iosFirebaseFid.isCompleted) {
        iosFirebaseFid.complete(fid?.takeIf { it.isNotBlank() })
    }
}

internal actual suspend fun fetchPlatformFid(): String? =
    withTimeoutOrNull(FID_WAIT_TIMEOUT_MILLIS) {
        iosFirebaseFid.await()
    }
