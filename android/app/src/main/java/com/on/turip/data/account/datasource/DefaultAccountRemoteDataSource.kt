package com.on.turip.data.account.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.account.dto.MyProfileResponse
import com.on.turip.data.account.service.AccountService
import com.on.turip.data.result.safeApiCall
import javax.inject.Inject

class DefaultAccountRemoteDataSource @Inject constructor(
    private val accountService: AccountService,
) : AccountRemoteDataSource {
    override suspend fun getMyProfile(): TuripResult<MyProfileResponse> = safeApiCall { accountService.getMyProfile() }
}
