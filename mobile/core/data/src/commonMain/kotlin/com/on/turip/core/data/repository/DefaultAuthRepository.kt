package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.AuthRefreshRemoteDataSource
import com.on.turip.core.data.datasource.AuthRemoteDataSource
import com.on.turip.core.domain.repository.AuthRepository
import com.on.turip.core.model.login.AuthResult
import com.on.turip.core.model.login.AuthTokens
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.mapCatching

class DefaultAuthRepository(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authRefreshRemoteDataSource: AuthRefreshRemoteDataSource,
) : AuthRepository {
    override suspend fun login(idToken: String): TuripResult<AuthResult> =
        authRemoteDataSource.postIdToken(idToken).mapCatching { it.toDomain() }

    override suspend fun requestTokens(refreshToken: String): TuripResult<AuthTokens> =
        authRefreshRemoteDataSource.postReissueToken(refreshToken).mapCatching { it.toDomain() }

    override suspend fun verifyToken(): TuripResult<Unit> = authRemoteDataSource.verifyToken()
}
