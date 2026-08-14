package com.on.turip.core.data.userstorage

import com.on.turip.core.data.datasource.UserStorageLocalDataSource
import com.on.turip.core.domain.repository.UserStorageRepository
import com.on.turip.core.model.login.AuthTokens
import com.on.turip.core.model.userstorage.TuripDeviceIdentifier
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class DefaultUserStorageRepository(
    private val localDataSource: UserStorageLocalDataSource,
) : UserStorageRepository {
    override suspend fun createId(turipDeviceIdentifier: TuripDeviceIdentifier) {
        localDataSource.createId(turipDeviceIdentifier.fid).recoverCancellation()
    }

    override suspend fun loadId(): Result<TuripDeviceIdentifier> =
        localDataSource
            .getId()
            .map { fid -> fid?.let(::TuripDeviceIdentifier) ?: TuripDeviceIdentifier.EMPTY }
            .recoverCancellation()

    override suspend fun loadAccessToken(): Result<String?> =
        localDataSource.getAccessToken().recoverCancellation()

    override suspend fun loadRefreshToken(): Result<String> =
        localDataSource.getRefreshToken().recoverCancellation()

    override suspend fun createTokens(tokens: AuthTokens): Result<Unit> =
        localDataSource
            .createTokens(tokens.accessToken, tokens.refreshToken)
            .recoverCancellation()

    override suspend fun clearTokens(): Result<Unit> =
        localDataSource.deleteTokens().recoverCancellation()
}

private suspend fun <T> Result<T>.recoverCancellation(): Result<T> {
    if (isFailure) {
        currentCoroutineContext().ensureActive()
    }
    return this
}
