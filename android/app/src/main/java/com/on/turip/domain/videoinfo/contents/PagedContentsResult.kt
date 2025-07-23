package com.on.turip.domain.videoinfo.contents

data class PagedContentsResult(
    val videos: List<VideoInformation>,
    val loadable: Boolean,
)
