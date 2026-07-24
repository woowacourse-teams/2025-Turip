package com.on.turip.core.model.content

import com.on.turip.core.model.content.video.VideoData
import com.on.turip.core.model.creator.Creator
import com.on.turip.core.model.region.City

data class Content(
    val id: Long,
    val creator: Creator,
    val videoData: VideoData,
    val city: City,
    val isBookmarked: Boolean,
)
