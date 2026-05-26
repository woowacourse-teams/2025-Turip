package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.account.AccountResponse

interface AccountDatasource {
    suspend fun getMyProfile(): AccountResponse

    suspend fun deleteAccount()
}
