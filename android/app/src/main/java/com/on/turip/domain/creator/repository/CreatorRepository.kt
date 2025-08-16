package com.on.turip.domain.creator.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.creator.Creator

interface CreatorRepository {
    suspend fun loadCreator(creatorId: Long): TuripCustomResult<Creator>
}
