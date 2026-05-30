package com.on.turip.core.data.repository

class DefaultDeferredDeepLinkRepository : DeferredDeepLinkRepository {
    override suspend fun resolveDeferredInvitationToken(): Result<String?> = Result.success(null)
}