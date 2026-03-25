package com.on.turip.data.invitation.repository

import com.on.turip.data.invitation.datasource.DeferredDeepLinkLocalDataSource
import com.on.turip.data.invitation.datasource.InstallReferrerDataSource
import com.on.turip.domain.invitation.InvitationTokenParser
import com.on.turip.domain.invitation.repository.DeferredDeepLinkRepository
import com.on.turip.domain.turip.TuripInvitationToken
import timber.log.Timber
import javax.inject.Inject

class DefaultDeferredDeepLinkRepository @Inject constructor(
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
                    Timber.e("InstallReferrer 조회 실패 (재시도 가능)")
                    return@runCatching null
                }
            val invitationToken: String? =
                InvitationTokenParser.extractTokenFromInstallReferrer(installReferrer)

            deferredDeepLinkLocalDataSource.markInstallReferrerHandled()
            invitationToken?.let(::TuripInvitationToken)
        }.onFailure { exception ->
            Timber.e(exception, "Deferred deep link 조회 실패")
        }
}
