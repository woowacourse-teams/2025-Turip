package com.on.turip.data.content.repository

import com.on.turip.data.content.dataSource.ContentRemoteDataSource
import com.on.turip.data.content.toDomain
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.userStorage.TuripDeviceIdentifier
import com.on.turip.domain.userStorage.repository.UserStorageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DefaultContentRepository(
    private val contentRemoteDataSource: ContentRemoteDataSource,
    private val userStorageRepository: UserStorageRepository,
) : ContentRepository {
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

    override suspend fun loadContentsSize(region: String): Result<Int> =
        contentRemoteDataSource
            .getContentsSize(region)
            .mapCatching { it.count }

    override suspend fun loadContents(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult> =
        contentRemoteDataSource
            .getContents(region, size, lastId)
            .mapCatching { it.toDomain() }

    override suspend fun loadContent(contentId: Long): Result<VideoData> =
        contentRemoteDataSource
            .getContentDetail(contentId, deviceIdentifier.fid)
            .mapCatching { it.toDomain() }
}
