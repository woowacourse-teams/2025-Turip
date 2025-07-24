package com.on.turip.data.trip

import com.on.turip.data.trip.dto.CategoryResponse
import com.on.turip.data.trip.dto.PlaceResponse
import com.on.turip.data.trip.dto.TripCourseResponse
import com.on.turip.data.trip.dto.TripDurationResponse
import com.on.turip.data.trip.dto.TripResponse
import com.on.turip.domain.videoinfo.contents.video.trip.Place
import com.on.turip.domain.videoinfo.contents.video.trip.Trip
import com.on.turip.domain.videoinfo.contents.video.trip.TripCourse
import com.on.turip.domain.videoinfo.contents.video.trip.TripDuration

fun TripResponse.toDomain(): Trip =
    Trip(
        tripDuration = tripDuration.toDomain(),
        tripPlaceCount = tripPlaceCount,
        tripCourses = tripCourses.map(TripCourseResponse::toDomain),
    )

fun TripDurationResponse.toDomain(): TripDuration =
    TripDuration(
        nights = nights,
        days = days,
    )

fun TripCourseResponse.toDomain(): TripCourse =
    TripCourse(
        tripCourseId = id,
        visitDay = visitDay,
        visitOrder = visitOrder,
        place = place.toDomain(),
    )

fun PlaceResponse.toDomain(): Place =
    Place(
        placeId = id,
        name = name,
        url = url,
        address = address,
        latitude = latitude,
        longitude = longitude,
        category = categories.map(CategoryResponse::name),
    )
