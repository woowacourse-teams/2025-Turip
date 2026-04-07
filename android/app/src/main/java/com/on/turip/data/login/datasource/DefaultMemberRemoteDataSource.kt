package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.service.MemberService
import com.on.turip.data.result.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultMemberRemoteDataSource @Inject constructor(
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
