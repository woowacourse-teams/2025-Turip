package com.on.turip.core.data.repository

import com.on.turip.core.domain.invitation.DeferredDeepLinkRepository

class DefaultDeferredDeepLinkRepository : DeferredDeepLinkRepository {
    override suspend fun resolveDeferredInvitationToken(): Result<String?> = Result.success(null)
}