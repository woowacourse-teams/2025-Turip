package com.on.turip.domain.accounts

import com.on.turip.core.result.TuripResult

interface AccountRepository {
    suspend fun loadMyProfile(): TuripResult<Account>
}
