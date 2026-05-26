package com.on.turip.core.data.mapper

import com.on.turip.core.model.Content
import com.on.turip.core.model.Creator
import com.on.turip.core.model.Place
import com.on.turip.core.model.UsersLikeContent
import com.on.turip.core.model.VideoData
import com.on.turip.core.network.dto.content.ContentResponse
import com.on.turip.core.network.dto.content.CreatorResponse
import com.on.turip.core.network.dto.content.PlaceResponse
import com.on.turip.core.network.dto.content.UsersLikeContentResponse
import com.on.turip.core.network.dto.content.VideoDataResponse

internal fun ContentResponse.toDomain() = Content(
    id = id,
    title = title,
    description = description,
    creator = creator.toDomain(),
    videoData = videoData.toDomain(),
    places = places.map { it.toDomain() },
    isBookmarked = isBookmarked,
)

internal fun CreatorResponse.toDomain() = Creator(
    id = id,
    name = name,
    profileImageUrl = profileImageUrl,
)

internal fun VideoDataResponse.toDomain() = VideoData(
    videoUrl = videoUrl,
    thumbnailUrl = thumbnailUrl,
)

internal fun PlaceResponse.toDomain() = Place(
    id = id,
    name = name,
    category = category,
    latitude = latitude,
    longitude = longitude,
)

internal fun UsersLikeContentResponse.toDomain() = UsersLikeContent(
    content = content.toDomain(),
    tripDuration = tripDuration,
)