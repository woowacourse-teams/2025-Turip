package com.on.turip.data.login.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.datasource.GuestDataSource
import com.on.turip.domain.login.GuestRepository
import javax.inject.Inject

class DefaultGuestRepository @Inject constructor(
    private val guestDataSource: GuestDataSource,
) : GuestRepository {
    override suspend fun deleteGuest(): TuripResult<Unit> = guestDataSource.deleteGuest()
}
