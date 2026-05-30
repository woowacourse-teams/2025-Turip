package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.account.MyProfileResponse

interface AccountDatasource {
    suspend fun getMyProfile(): MyProfileResponse

    suspend fun deleteAccount()
}
