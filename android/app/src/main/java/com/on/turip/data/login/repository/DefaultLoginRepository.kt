package com.on.turip.data.login.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.data.login.datasource.LoginDatasource
import com.on.turip.data.login.toDomain
import com.on.turip.domain.login.AuthTokens
import com.on.turip.domain.login.LoginRepository
import javax.inject.Inject

class DefaultLoginRepository @Inject constructor(
    val loginDatasource: LoginDatasource,
) : LoginRepository {
    override suspend fun login(idToken: String): TuripCustomResult<AuthTokens> =
        loginDatasource.postIdToken(idToken).mapCatching { it.toDomain() }
}
