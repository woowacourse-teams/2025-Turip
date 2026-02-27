package com.on.turip.data.accounts.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.accounts.datasource.AccountRemoteDataSource
import com.on.turip.data.accounts.toDomain
import com.on.turip.domain.accounts.Account
import com.on.turip.domain.accounts.AccountRepository
import javax.inject.Inject

class DefaultAccountRepository @Inject constructor(
    private val accountRemoteDataSource: AccountRemoteDataSource,
) : AccountRepository {
    override suspend fun loadMyProfile(): TuripResult<Account> = accountRemoteDataSource.getMyProfile().mapCatching { it.toDomain() }
}
