package com.on.turip.core.domain.repository

interface DeferredDeepLinkRepository {
    suspend fun resolveDeferredInvitationToken(): Result<String?>
}
