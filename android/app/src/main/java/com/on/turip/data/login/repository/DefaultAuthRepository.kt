package com.on.turip.data.login.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.login.datasource.AuthRefreshRemoteDataSource
import com.on.turip.data.login.datasource.AuthRemoteDataSource
import com.on.turip.data.login.toDomain
import com.on.turip.domain.login.AuthRepository
import com.on.turip.domain.login.AuthResult
import com.on.turip.domain.login.AuthTokens
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authRefreshRemoteDataSource: AuthRefreshRemoteDataSource,
) : AuthRepository {
    override suspend fun login(idToken: String): TuripResult<AuthResult> =
        authRemoteDataSource.postIdToken(idToken).mapCatching { it.toDomain() }

    override suspend fun requestTokens(refreshToken: String): TuripResult<AuthTokens> =
        authRefreshRemoteDataSource.postReissueToken(refreshToken).mapCatching { it.toDomain() }

    override suspend fun verifyToken(): TuripResult<Unit> = authRemoteDataSource.verifyToken()
}
