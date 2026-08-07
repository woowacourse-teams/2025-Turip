package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.GuestRemoteDataSource
import com.on.turip.core.model.result.TuripResult
import com.on.turip.domain.login.GuestRepository

class DefaultGuestRepository(
    private val guestRemoteDataSource: GuestRemoteDataSource,
) : GuestRepository {
    override suspend fun deleteGuest(): TuripResult<Unit> = guestRemoteDataSource.deleteGuest()
}
