package com.on.turip.core.domain.repository

import com.on.turip.core.model.Member

interface AccountRepository {
    suspend fun getMyProfile(): Result<Member>

    suspend fun deleteAccount(): Result<Unit>
}
