package com.on.turip.core.data.repository

import com.on.turip.core.data.dto.content.CityResponse
import com.on.turip.core.data.dto.content.ContentDetailResponse
import com.on.turip.core.data.dto.content.ContentInformationResponse
import com.on.turip.core.data.dto.content.ContentPlaceResponse
import com.on.turip.core.data.dto.content.ContentPlacesResponse
import com.on.turip.core.data.dto.content.ContentResponse
import com.on.turip.core.data.dto.content.ContentsInformationResponse
import com.on.turip.core.data.dto.content.CreatorInformationResponse
import com.on.turip.core.data.dto.content.PlaceResponse
import com.on.turip.core.data.dto.content.TripDurationInformationResponse
import com.on.turip.core.data.dto.content.TripDurationResponse
import com.on.turip.core.data.dto.content.UsersLikeContentResponse
import com.on.turip.core.data.dto.content.UsersLikeContentsResponse
import com.on.turip.core.data.dto.creator.CreatorResponse
import com.on.turip.core.model.content.Content
import com.on.turip.core.model.content.PagedContentsResult
import com.on.turip.core.model.content.UsersLikeContent
import com.on.turip.core.model.content.video.VideoData
import com.on.turip.core.model.content.video.VideoInformation
import com.on.turip.core.model.creator.Creator
import com.on.turip.core.model.region.City
import com.on.turip.core.model.trip.ContentPlace
import com.on.turip.core.model.trip.Place
import com.on.turip.core.model.trip.Trip
import com.on.turip.core.model.trip.TripDuration

fun ContentsInformationResponse.toDomain(): PagedContentsResult =
    PagedContentsResult(
        videos = contentsInformation.map { it.toDomain() },
        loadable = loadable,
    )

fun ContentInformationResponse.toDomain(): VideoInformation =
    VideoInformation(
        content = content.toDomain(),
        trip = Trip(
            tripDuration = tripDuration.toDomain(),
            tripPlaceCount = tripPlaceCount,
        ),
    )

fun ContentDetailResponse.toDomain(): Content =
    Content(
        id = id,
        creator = creator.toDomain(),
        videoData = VideoData(
            title = title,
            url = url,
            uploadedDate = uploadedDate,
        ),
        city = city.toDomain(),
        isBookmarked = isBookmarked,
    )

fun ContentResponse.toDomain(): Content =
    Content(
        id = id,
        creator = creator.toDomain(),
        videoData = VideoData(
            title = title,
            url = url,
            uploadedDate = uploadedDate,
        ),
        city = city.toDomain(),
        isBookmarked = isBookmarked,
    )

fun UsersLikeContentsResponse.toDomain(): List<UsersLikeContent> = contents.map { it.toDomain() }

fun UsersLikeContentResponse.toDomain(): UsersLikeContent =
    UsersLikeContent(
        content = content.toDomain(),
        tripDuration = tripDuration.toDomain(),
    )

fun ContentPlacesResponse.toDomain(): Trip =
    Trip(
        tripDuration = tripDuration.toDomain(),
        tripPlaceCount = contentPlaceCount,
        contentPlaces = contentPlaces.map { it.toDomain() },
    )

fun ContentPlaceResponse.toDomain(): ContentPlace =
    ContentPlace(
        tripCourseId = id,
        visitDay = visitDay,
        visitOrder = visitOrder,
        place = place.toDomain(),
        timeLine = timeLine,
        isTuripPlace = isTuripPlace,
    )

fun PlaceResponse.toDomain(): Place =
    Place(
        placeId = id,
        name = name,
        url = url,
        address = address,
        latitude = latitude,
        longitude = longitude,
        category = categories.map { it.name },
    )

fun CreatorInformationResponse.toDomain(): Creator =
    Creator(
        id = id,
        channelName = channelName,
        profileImage = profileImage,
    )

fun CreatorResponse.toDomain(): Creator =
    Creator(
        id = id,
        channelName = channelName,
        profileImage = profileImage,
    )

fun CityResponse.toDomain(): City = City(name = name)

fun TripDurationInformationResponse.toDomain(): TripDuration =
    TripDuration(
        nights = nights,
        days = days,
    )

fun TripDurationResponse.toDomain(): TripDuration =
    TripDuration(
        nights = nights,
        days = days,
    )
