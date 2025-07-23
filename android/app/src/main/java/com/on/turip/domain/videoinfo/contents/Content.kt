package com.on.turip.domain.videoinfo.contents

import com.on.turip.domain.videoinfo.contents.creator.Creator

data class Content(
    val id: Long,
    val creator: Creator,
    val videoData: VideoData,
)
