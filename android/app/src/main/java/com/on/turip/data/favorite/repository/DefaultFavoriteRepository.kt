package com.on.turip.data.favorite.repository

import com.on.turip.data.favorite.dataSource.FavoriteRemoteDataSource
import com.on.turip.data.favorite.toRequestDto
import com.on.turip.domain.favorite.repository.FavoriteRepository
import com.on.turip.domain.userStorage.TuripDeviceIdentifier
import com.on.turip.domain.userStorage.repository.UserStorageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DefaultFavoriteRepository(
    private val favoriteRemoteDataSource: FavoriteRemoteDataSource,
    private val userStorageRepository: UserStorageRepository,
) : FavoriteRepository {
    private lateinit var deviceIdentifier: TuripDeviceIdentifier

    init {
        CoroutineScope(Dispatchers.IO).launch {
            userStorageRepository
                .loadId()
                .onSuccess { turipDeviceIdentifier: TuripDeviceIdentifier ->
                    deviceIdentifier = turipDeviceIdentifier
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    override suspend fun createFavorite(contentId: Long): Result<Unit> =
        favoriteRemoteDataSource.postFavorite(deviceIdentifier.fid, contentId.toRequestDto())

    override suspend fun deleteFavorite(contentId: Long): Result<Unit> =
        favoriteRemoteDataSource.deleteFavorite(deviceIdentifier.fid, contentId)
}
