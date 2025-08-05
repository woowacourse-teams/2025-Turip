package com.on.turip.domain.content

import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.creator.Creator

data class Content(
    val id: Long,
    val creator: Creator,
    val videoData: VideoData,
    val city: City,
)
