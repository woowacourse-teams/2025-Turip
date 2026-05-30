package com.on.turip.core.domain.repository

import com.on.turip.core.model.account.Account
import com.on.turip.core.model.result.TuripResult

interface AccountRepository {
    suspend fun loadMyProfile(): TuripResult<Account>
}
