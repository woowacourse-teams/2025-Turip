package com.on.turip.domain.videoinfo.contents

import com.on.turip.domain.videoinfo.contents.video.VideoInformation

data class PagedContentsResult(
    val videos: List<VideoInformation>,
    val loadable: Boolean,
)
