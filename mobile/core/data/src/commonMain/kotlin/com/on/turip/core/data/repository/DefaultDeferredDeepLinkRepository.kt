package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.DeferredDeepLinkLocalDataSource
import com.on.turip.core.data.datasource.InstallReferrerDataSource
import com.on.turip.core.domain.repository.DeferredDeepLinkRepository
import com.on.turip.core.model.invitation.InvitationTokenParser
import com.on.turip.core.model.turip.TuripInvitationToken
import io.github.aakira.napier.Napier

class DefaultDeferredDeepLinkRepository(
    private val installReferrerDataSource: InstallReferrerDataSource,
    private val deferredDeepLinkLocalDataSource: DeferredDeepLinkLocalDataSource,
) : DeferredDeepLinkRepository {
    override suspend fun resolveDeferredInvitationToken(): Result<TuripInvitationToken?> =
        runCatching {
            if (deferredDeepLinkLocalDataSource.isInstallReferrerHandled()) return@runCatching null

            val installReferrerResult: Result<String?> =
                installReferrerDataSource.getInstallReferrer()
            val installReferrer: String? =
                installReferrerResult.getOrElse {
                    Napier.e("InstallReferrer 조회 실패 (재시도 가능)")
                    return@runCatching null
                }
            val invitationToken: String? =
                InvitationTokenParser.extractTokenFromInstallReferrer(installReferrer)

            deferredDeepLinkLocalDataSource.markInstallReferrerHandled()
            invitationToken?.let(::TuripInvitationToken)
        }.onFailure { exception ->
            Napier.e("Deferred deep link 조회 실패")
        }
}
