package com.on.turip.data.favorite.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.data.favorite.datasource.FavoriteRemoteDataSource
import com.on.turip.data.favorite.toDomain
import com.on.turip.data.favorite.toRequestDto
import com.on.turip.domain.favorite.PagedFavoriteContents
import com.on.turip.domain.favorite.repository.FavoriteRepository
import com.on.turip.domain.userstorage.TuripDeviceIdentifier
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import timber.log.Timber

class DefaultFavoriteRepository(
    private val favoriteRemoteDataSource: FavoriteRemoteDataSource,
    private val userStorageRepository: UserStorageRepository,
) : FavoriteRepository {
    override suspend fun createFavorite(contentId: Long): TuripCustomResult<Unit> {
        val turipDeviceIdentifier: TuripDeviceIdentifier =
            CoroutineScope(Dispatchers.IO)
                .async {
                    userStorageRepository
                        .loadId()
                        .onFailure {
                            Timber.e("${it.message}")
                        }
                }.await()
                .getOrThrow()
        return favoriteRemoteDataSource.postFavorite(
            turipDeviceIdentifier.fid,
            contentId.toRequestDto(),
        )
    }

    override suspend fun deleteFavorite(contentId: Long): TuripCustomResult<Unit> {
        val turipDeviceIdentifier: TuripDeviceIdentifier =
            CoroutineScope(Dispatchers.IO)
                .async {
                    userStorageRepository
                        .loadId()
                        .onFailure {
                            Timber.e("${it.message}")
                        }
                }.await()
                .getOrThrow()
        return favoriteRemoteDataSource.deleteFavorite(turipDeviceIdentifier.fid, contentId)
    }

    override suspend fun loadFavoriteContents(
        size: Int,
        lastId: Long,
    ): TuripCustomResult<PagedFavoriteContents> {
        val turipDeviceIdentifier: TuripDeviceIdentifier =
            CoroutineScope(Dispatchers.IO)
                .async {
                    userStorageRepository
                        .loadId()
                        .onFailure {
                            Timber.e("${it.message}")
                        }
                }.await()
                .getOrThrow()

        return favoriteRemoteDataSource
            .getFavoriteContents(
                turipDeviceIdentifier.fid,
                size,
                lastId,
            ).mapCatching { it.toDomain() }
    }
}
