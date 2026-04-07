package com.on.turip.data.account.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.account.dto.MyProfileResponse
import com.on.turip.data.account.service.AccountService
import com.on.turip.data.result.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultAccountRemoteDataSource @Inject constructor(
    private val accountService: AccountService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : AccountRemoteDataSource {
    override suspend fun getMyProfile(): TuripResult<MyProfileResponse> =
        withContext(coroutineContext) {
            safeApiCall { accountService.getMyProfile() }
        }
}
