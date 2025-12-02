package com.on.turip.data.login.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.data.login.datasource.AuthDataSource
import com.on.turip.data.login.toDomain
import com.on.turip.domain.login.AuthRepository
import com.on.turip.domain.login.AuthResult
import com.on.turip.domain.login.AuthTokens
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun login(idToken: String): TuripCustomResult<AuthResult> =
        authDataSource.postIdToken(idToken).mapCatching { it.toDomain() }

    override suspend fun requestTokens(refreshToken: String): TuripCustomResult<AuthTokens> =
        authDataSource.postReissueToken(refreshToken).mapCatching { it.toDomain() }

    override suspend fun getTokenVerification(accessToken: String): TuripCustomResult<Unit> =
        authDataSource.getTokenVerification(accessToken)
}
