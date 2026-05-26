package com.on.turip.core.model

data class Content(
    val id: Long,
    val title: String,
    val description: String,
    val creator: Creator,
    val videoData: VideoData,
    val places: List<Place>,
    val isBookmarked: Boolean,
)
