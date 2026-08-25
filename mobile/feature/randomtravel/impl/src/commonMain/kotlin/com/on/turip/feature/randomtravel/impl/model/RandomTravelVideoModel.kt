package com.on.turip.feature.randomtravel.impl.model

import androidx.compose.runtime.Immutable

@Immutable
data class RandomTravelVideoModel(
    val contentId: Long,
    val title: String,
    val thumbnailUrl: String,
    val channelName: String,
    val profileImageUrl: String,
    val uploadedDate: String,
    val cityName: String,
    val nights: Int,
    val days: Int,
    val placeCount: Int,
)
