package com.on.turip.domain.videoinfo.contents.creator.repository

import com.on.turip.domain.videoinfo.contents.creator.Creator

interface CreatorRepository {
    suspend fun loadCreator(creatorId: Long): Creator
}
