package com.on.turip.core.domain.repository

interface InvitationRepository {
    suspend fun fetchInvitationInfo(token: String): Result<TuripInvitationInformation>

    suspend fun joinTurip(turipId: Long): Result<Unit>
}
