package com.on.turip.data.content

import com.on.turip.data.content.dto.ContentInformationResponse
import com.on.turip.data.content.dto.ContentResponse
import com.on.turip.data.content.dto.ContentsInformationResponse
import com.on.turip.data.content.dto.CreatorResponse
import com.on.turip.data.content.dto.TripDurationResponse
import com.on.turip.domain.contents.Content
import com.on.turip.domain.contents.Creator
import com.on.turip.domain.contents.PagedContentsResult
import com.on.turip.domain.contents.TripDuration
import com.on.turip.domain.contents.VideoInformation

fun ContentsInformationResponse.toDomain(): PagedContentsResult =
    PagedContentsResult(
        videos = contentsInformation.map(ContentInformationResponse::toDomain),
        loadable = loadable,
    )

fun ContentInformationResponse.toDomain(): VideoInformation =
    VideoInformation(
        content = content.toDomain(),
        tripDuration = tripDuration.toDomain(),
        tripPlaceCount = tripPlaceCount,
    )

fun ContentResponse.toDomain(): Content =
    Content(
        id = id,
        creator = creator.toDomain(),
        title = title,
        url = url,
        uploadedDate = uploadedDate,
    )

fun TripDurationResponse.toDomain(): TripDuration =
    TripDuration(
        nights = nights,
        days = days,
    )

fun CreatorResponse.toDomain(): Creator =
    Creator(
        id = id,
        channelName = channelName,
        profileImage = profileImage,
    )
