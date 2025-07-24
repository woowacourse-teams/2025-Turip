package com.on.turip.data.creator.repository

import com.on.turip.data.creator.dataSource.CreatorRemoteDataSource
import com.on.turip.data.creator.toDomain
import com.on.turip.domain.videoinfo.contents.creator.Creator
import com.on.turip.domain.videoinfo.contents.creator.repository.CreatorRepository

class DefaultCreatorRepository(
    private val creatorRemoteDataSource: CreatorRemoteDataSource,
) : CreatorRepository {
    override suspend fun loadCreator(creatorId: Long): Result<Creator> =
        creatorRemoteDataSource
            .getCreator(creatorId)
            .mapCatching { it.toDomain() }
}
