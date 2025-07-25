package com.on.turip.domain.creator.repository

import com.on.turip.domain.creator.Creator

interface CreatorRepository {
    suspend fun loadCreator(creatorId: Long): Result<Creator>
}
