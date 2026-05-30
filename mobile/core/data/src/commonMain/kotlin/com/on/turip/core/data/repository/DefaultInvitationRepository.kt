package com.on.turip.core.data.repository

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.data.mapper.toDomain
import com.on.turip.core.network.datasource.TuripDatasource

class DefaultInvitationRepository(
    private val turipDatasource: TuripDatasource,
) : InvitationRepository {
    override suspend fun fetchInvitationInfo(token: String): Result<TuripInvitationInformation> =
        safeApiCall { turipDatasource.getInvitationInformation(token).toDomain() }

    override suspend fun joinTurip(turipId: Long): Result<Unit> =
        safeApiCall { turipDatasource.joinTurip(turipId) }
}