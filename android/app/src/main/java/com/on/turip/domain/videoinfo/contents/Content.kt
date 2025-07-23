package com.on.turip.domain.videoinfo.contents

import com.on.turip.domain.videoinfo.contents.creator.Creator

data class Content(
    val id: Long,
    val creator: Creator,
    val title: String,
    val url: String,
    val uploadedDate: String,
)
