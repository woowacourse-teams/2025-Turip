package com.on.turip.data.accounts.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.accounts.dto.MyProfileResponse
import com.on.turip.data.accounts.service.AccountService
import com.on.turip.data.result.safeApiCall
import javax.inject.Inject

class DefaultAccountRemoteDataSource @Inject constructor(
    private val accountService: AccountService,
) : AccountRemoteDataSource {
    override suspend fun getMyProfile(): TuripResult<MyProfileResponse> = safeApiCall { accountService.getMyProfile() }
}
