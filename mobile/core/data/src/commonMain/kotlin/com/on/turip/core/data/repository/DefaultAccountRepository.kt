package com.on.turip.core.data.repository

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.domain.account.AccountRepository
import com.on.turip.core.model.Member
import com.on.turip.core.network.datasource.AccountDatasource

class DefaultAccountRepository(
    private val accountDatasource: AccountDatasource,
) : AccountRepository {
    override suspend fun getMyProfile(): Result<Member> =
        safeApiCall {
            accountDatasource.getMyProfile().let {
                Member(id = it.id, nickname = it.nickname, profileImageUrl = it.profileImageUrl)
            }
        }

    override suspend fun deleteAccount(): Result<Unit> =
        safeApiCall { accountDatasource.deleteAccount() }
}