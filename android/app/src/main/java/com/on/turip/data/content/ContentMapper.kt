package com.on.turip.data.content

import com.on.turip.data.content.dto.CityResponse
import com.on.turip.data.content.dto.ContentDetailResponse
import com.on.turip.data.content.dto.ContentInformationResponse
import com.on.turip.data.content.dto.ContentInformationResponse2
import com.on.turip.data.content.dto.ContentResponse
import com.on.turip.data.content.dto.ContentResponse2
import com.on.turip.data.content.dto.ContentsInformationResponse
import com.on.turip.data.content.dto.ContentsInformationResponse2
import com.on.turip.data.content.dto.CreatorInformationResponse
import com.on.turip.data.content.dto.TripDurationInformationResponse
import com.on.turip.data.content.dto.UsersLikeContentResponse
import com.on.turip.data.content.dto.UsersLikeContentsResponse
import com.on.turip.data.creator.toDomain
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.content.video.VideoInformation
import com.on.turip.domain.creator.Creator
import com.on.turip.domain.region.City
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.TripDuration

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
        city = city.toDomain(),
        isFavorite = isFavorite,
    )

fun CityResponse.toDomain(): City =
    City(
        name = name,
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

fun ContentDetailResponse.toDomain(): Content =
    Content(
        id = id,
        creator = creator.toDomain(),
        videoData =
            VideoData(
                title = title,
                url = url,
                uploadedDate = uploadedDate,
            ),
        city = city.toDomain(),
        isFavorite = isFavorite,
    )

fun UsersLikeContentsResponse.toDomain(): List<UsersLikeContent> = contents.map { it.toDomain() }

fun UsersLikeContentResponse.toDomain(): UsersLikeContent =
    UsersLikeContent(
        content = content.toDomain(),
        tripDuration = tripDuration.toDomain(),
    )

fun ContentsInformationResponse2.toDomain(): PagedContentsResult =
    PagedContentsResult(
        videos = contentsInformation.map(ContentInformationResponse2::toDomain),
        loadable = loadable,
    )

fun ContentInformationResponse2.toDomain(): VideoInformation =
    VideoInformation(
        content = content.toDomain(),
        trip =
            Trip(
                tripDuration = tripDuration.toDomain(),
                tripPlaceCount = tripPlaceCount,
            ),
    )

fun ContentResponse2.toDomain(): Content =
    Content(
        id = id,
        creator = creator.toDomain(),
        videoData =
            VideoData(
                title = title,
                url = url,
                uploadedDate = uploadedDate,
            ),
        city = City(name = city),
        isFavorite = true,
    )
