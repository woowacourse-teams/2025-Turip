package com.on.turip.domain.content

import com.on.turip.domain.content.video.VideoInformation

data class PagedContentsResult(
    val videos: List<VideoInformation>,
    val loadable: Boolean,
)
