package com.on.turip.data.content.repository

import com.on.turip.domain.videoinfo.contents.creator.Creator
import com.on.turip.domain.videoinfo.contents.creator.repository.CreatorRepository

class DefaultCreatorRepository : CreatorRepository {
    override suspend fun loadCreator(creatorId: Long): Result<Creator> {
        TODO("Not yet implemented")
    }
}
