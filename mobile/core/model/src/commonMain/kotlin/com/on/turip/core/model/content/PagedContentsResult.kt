package com.on.turip.core.model.content

import com.on.turip.core.model.content.video.VideoInformation

data class PagedContentsResult(
    val videos: List<VideoInformation>,
    val loadable: Boolean,
)
