package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.AccountRemoteDataSource
import com.on.turip.core.domain.repository.AccountRepository
import com.on.turip.core.model.account.Account
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.mapCatching

class DefaultAccountRepository(
    private val accountRemoteDataSource: AccountRemoteDataSource,
) : AccountRepository {
    override suspend fun loadMyProfile(): TuripResult<Account> = accountRemoteDataSource.getMyProfile().mapCatching { it.toDomain() }
}
