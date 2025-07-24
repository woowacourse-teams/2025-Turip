package com.on.turip.data.content

import com.on.turip.data.content.dto.ContentDetailResponse
import com.on.turip.data.content.dto.ContentInformationResponse
import com.on.turip.data.content.dto.ContentResponse
import com.on.turip.data.content.dto.ContentsInformationResponse
import com.on.turip.data.content.dto.CreatorInformationResponse
import com.on.turip.data.content.dto.TripDurationInformationResponse
import com.on.turip.domain.videoinfo.contents.Content
import com.on.turip.domain.videoinfo.contents.PagedContentsResult
import com.on.turip.domain.videoinfo.contents.VideoData
import com.on.turip.domain.videoinfo.contents.creator.Creator
import com.on.turip.domain.videoinfo.contents.video.VideoInformation
import com.on.turip.domain.videoinfo.contents.video.trip.Trip
import com.on.turip.domain.videoinfo.contents.video.trip.TripDuration

fun ContentsInformationResponse.toDomain(): PagedContentsResult =
    PagedContentsResult(
        videos = contentsInformation.map(ContentInformationResponse::toDomain),
        loadable = loadable,
    )

fun ContentInformationResponse.toDomain(): VideoInformation =
    VideoInformation(
        content = content.toDomain(),
        trip =
            Trip(
                tripDuration = tripDuration.toDomain(),
                tripPlaceCount = tripPlaceCount,
            ),
    )

fun ContentResponse.toDomain(): Content =
    Content(
        id = id,
        creator = creator.toDomain(),
        videoData =
            VideoData(
                title = title,
                url = url,
                uploadedDate = uploadedDate,
            ),
    )

fun TripDurationInformationResponse.toDomain(): TripDuration =
    TripDuration(
        nights = nights,
        days = days,
    )

fun CreatorInformationResponse.toDomain(): Creator =
    Creator(
        id = id,
        channelName = channelName,
        profileImage = profileImage,
    )

fun ContentDetailResponse.toDomain(): VideoData =
    VideoData(
        title = title,
        url = url,
        uploadedDate = uploadedDate,
    )
