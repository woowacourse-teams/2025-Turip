package com.on.turip.data.creator

import com.on.turip.data.creator.dto.CreatorResponse
import com.on.turip.domain.videoinfo.contents.creator.Creator

fun CreatorResponse.toDomain(): Creator =
    Creator(
        id = id,
        channelName = channelName,
        profileImage = profileImage,
    )
