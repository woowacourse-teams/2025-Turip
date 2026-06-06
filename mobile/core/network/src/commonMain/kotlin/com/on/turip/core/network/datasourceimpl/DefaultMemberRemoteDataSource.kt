package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.datasource.MemberRemoteDataSource
import com.on.turip.core.network.service.MemberService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultMemberRemoteDataSource(
    private val memberService: MemberService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : MemberRemoteDataSource {
    override suspend fun postMigration(): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { memberService.postMigration() }
        }

    override suspend fun postLogout(): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { memberService.postLogout() }
        }

    override suspend fun deleteMember(): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { memberService.deleteMember() }
        }

    override suspend fun postMigrationReject(): TuripResult<Unit> =
        safeApiCall {
            memberService.postMigrationReject()
        }
}
