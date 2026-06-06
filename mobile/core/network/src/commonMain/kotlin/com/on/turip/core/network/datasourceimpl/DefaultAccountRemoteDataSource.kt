package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.datasource.AccountRemoteDataSource
import com.on.turip.core.network.dto.account.MyProfileResponse
import com.on.turip.core.network.service.AccountService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultAccountRemoteDataSource(
    private val accountService: AccountService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : AccountRemoteDataSource {
    override suspend fun getMyProfile(): TuripResult<MyProfileResponse> =
        withContext(coroutineContext) {
            safeApiCall { accountService.getMyProfile() }
        }
}
