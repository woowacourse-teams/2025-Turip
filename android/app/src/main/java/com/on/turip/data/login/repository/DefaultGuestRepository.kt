package com.on.turip.data.login.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.datasource.GuestRemoteDataSource
import com.on.turip.domain.login.GuestRepository
import javax.inject.Inject

class DefaultGuestRepository @Inject constructor(
    private val guestRemoteDataSource: GuestRemoteDataSource,
) : GuestRepository {
    override suspend fun deleteGuest(): TuripResult<Unit> = guestRemoteDataSource.deleteGuest()
}
