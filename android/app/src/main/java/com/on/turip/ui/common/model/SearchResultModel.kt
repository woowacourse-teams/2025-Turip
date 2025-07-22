package com.on.turip.ui.common.model

import com.on.turip.domain.contents.Content
import com.on.turip.domain.contents.TripDuration

data class SearchResultModel(
    val content: Content,
    val tripDuration: TripDuration,
    val tripPlaceCount: Int,
)
