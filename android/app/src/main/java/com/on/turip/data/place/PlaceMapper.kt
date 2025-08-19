package com.on.turip.data.place

import com.on.turip.data.place.dto.FavoritePlacesResponse
import com.on.turip.domain.trip.Place

fun FavoritePlacesResponse.toDomain(): List<Place> =
    favoritePlaceResponses.map { place ->
        with(place.placeResponse) {
            Place(
                placeId = id,
                name = name,
                url = url,
                address = address,
                latitude = latitude,
                longitude = longitude,
                category = categories.map { it.name },
            )
        }
    }
