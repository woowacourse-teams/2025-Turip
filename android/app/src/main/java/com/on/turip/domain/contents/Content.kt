package com.on.turip.domain.contents

data class Content(
    val id: Long,
    val creator: Creator,
    val title: String,
    val url: String,
    val uploadedDate: String,
)
