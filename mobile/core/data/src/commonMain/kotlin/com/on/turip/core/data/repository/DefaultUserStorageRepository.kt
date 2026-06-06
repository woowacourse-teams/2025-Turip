package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.UserStorageLocalDataSource
import com.on.turip.core.data.mapper.toDomain
import com.on.turip.core.domain.repository.UserStorageRepository
import com.on.turip.core.model.login.AuthTokens
import com.on.turip.core.model.userstorage.TuripDeviceIdentifier
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DefaultUserStorageRepository(
    private val userStorageLocalDataSource: UserStorageLocalDataSource,
) : UserStorageRepository {
    override suspend fun createId(turipDeviceIdentifier: TuripDeviceIdentifier) {
        userStorageLocalDataSource.createId(turipDeviceIdentifier.fid)
    }

    override suspend fun loadId(): Result<TuripDeviceIdentifier> =
        userStorageLocalDataSource
            .getId()
            .mapCatching { fid: String? -> fid?.toDomain() ?: TuripDeviceIdentifier.EMPTY }
            .onFailure {
                Napier.e("${it.message}")
            }

    override suspend fun loadAccessToken(): Result<String?> = userStorageLocalDataSource.getAccessToken()

    override suspend fun loadRefreshToken(): Result<String> = userStorageLocalDataSource.getRefreshToken()

    override suspend fun createTokens(tokens: AuthTokens): Result<Unit> =
        withContext(Dispatchers.IO) {
            userStorageLocalDataSource.createAccessToken(tokens.accessToken)
            userStorageLocalDataSource.createRefreshToken(tokens.refreshToken)
        }

    override suspend fun clearTokens(): Result<Unit> =
        withContext(Dispatchers.IO) {
            userStorageLocalDataSource.deleteTokens()
        }
}
