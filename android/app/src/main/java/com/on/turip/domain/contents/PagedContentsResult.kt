package com.on.turip.domain.contents

data class PagedContentsResult(
    val videos: List<Video>,
    val loadable: Boolean,
)
