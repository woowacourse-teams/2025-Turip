package com.on.turip.data.account.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.account.datasource.AccountRemoteDataSource
import com.on.turip.data.account.toDomain
import com.on.turip.domain.account.Account
import com.on.turip.domain.account.AccountRepository
import javax.inject.Inject

class DefaultAccountRepository @Inject constructor(
    private val accountRemoteDataSource: AccountRemoteDataSource,
) : AccountRepository {
    override suspend fun loadMyProfile(): TuripResult<Account> = accountRemoteDataSource.getMyProfile().mapCatching { it.toDomain() }
}
