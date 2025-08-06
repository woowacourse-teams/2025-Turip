package com.on.turip.data.creator.dataSource

import com.on.turip.data.creator.dto.CreatorResponse

interface CreatorRemoteDataSource {
    suspend fun getCreator(creatorId: Long): Result<CreatorResponse>
}
