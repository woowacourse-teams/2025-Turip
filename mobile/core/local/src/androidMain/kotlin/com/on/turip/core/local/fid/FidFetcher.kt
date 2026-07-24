package com.on.turip.core.local.fid

import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.tasks.await

internal actual suspend fun fetchPlatformFid(): String? =
    runCatching { FirebaseInstallations.getInstance().id.await() }.getOrNull()
