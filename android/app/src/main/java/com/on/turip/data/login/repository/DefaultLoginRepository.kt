package com.on.turip.data.login.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.data.login.datasource.LoginDatasource
import com.on.turip.data.login.dto.LoginJwtTokenResponse
import com.on.turip.data.userstorage.datasource.UserStorageLocalDataSource
import com.on.turip.domain.login.LoginRepository
import javax.inject.Inject

class DefaultLoginRepository @Inject constructor(
    val userStorageLocalDataSource: UserStorageLocalDataSource,
    val loginDatasource: LoginDatasource,
) : LoginRepository {
    override suspend fun login(idToken: String): TuripCustomResult<Unit> =
        loginDatasource.postIdToken(idToken).mapCatching { result: LoginJwtTokenResponse ->
            userStorageLocalDataSource.createAccessToken(result.accessToken).getOrThrow()
            userStorageLocalDataSource.createRefreshToken(result.refreshToken).getOrThrow()
        }
}
