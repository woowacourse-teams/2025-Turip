package com.on.turip.core.domain.invitation

interface DeferredDeepLinkRepository {
    suspend fun resolveDeferredInvitationToken(): Result<String?>
}
