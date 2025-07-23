package com.on.turip.domain.contents

data class PagedContentsResult(
    val videos: List<VideoInformation>,
    val loadable: Boolean,
)
