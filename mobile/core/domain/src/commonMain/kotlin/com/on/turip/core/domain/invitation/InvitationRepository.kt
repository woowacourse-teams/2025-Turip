package com.on.turip.core.domain.invitation

import com.on.turip.core.model.TuripInvitationInformation

interface InvitationRepository {
    suspend fun fetchInvitationInfo(token: String): Result<TuripInvitationInformation>
    suspend fun joinTurip(turipId: Long): Result<Unit>
}
