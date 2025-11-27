package com.on.turip.data.login.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.data.login.datasource.LoginDatasource
import com.on.turip.data.login.toDomain
import com.on.turip.domain.login.AuthResult
import com.on.turip.domain.login.AuthTokens
import com.on.turip.domain.login.LoginRepository
import javax.inject.Inject

class DefaultLoginRepository @Inject constructor(
    val loginDatasource: LoginDatasource,
) : LoginRepository {
    override suspend fun login(idToken: String): TuripCustomResult<AuthResult> =
        loginDatasource.postIdToken(idToken).mapCatching { it.toDomain() }

    override suspend fun requestTokens(refreshToken: String): TuripCustomResult<AuthTokens> =
        loginDatasource.postReNewToken(refreshToken).mapCatching { it.toDomain() }
}
