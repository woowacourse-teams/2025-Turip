package com.on.turip.feature.popularregion.impl.model

import androidx.compose.runtime.Immutable

@Immutable
data class RegionContentModel(
    val contentId: Long,
    val title: String,
    val thumbnailUrl: String,
    val creatorName: String,
    val uploadedDate: String,
    val nights: Int,
    val days: Int,
    val placeCount: Int,
    val isBookmarked: Boolean,
) {
    val isDayTrip: Boolean = nights == 0
}
