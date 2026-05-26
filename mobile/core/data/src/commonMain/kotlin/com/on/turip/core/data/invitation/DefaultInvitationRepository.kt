package com.on.turip.core.data.invitation

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.data.turip.toDomain
import com.on.turip.core.domain.invitation.InvitationRepository
import com.on.turip.core.model.TuripInvitationInformation
import com.on.turip.core.network.datasource.TuripDatasource

class DefaultInvitationRepository(
    private val turipDatasource: TuripDatasource,
) : InvitationRepository {
    override suspend fun fetchInvitationInfo(token: String): Result<TuripInvitationInformation> =
        safeApiCall { turipDatasource.getInvitationInformation(token).toDomain() }

    override suspend fun joinTurip(turipId: Long): Result<Unit> =
        safeApiCall { turipDatasource.joinTurip(turipId) }
}
