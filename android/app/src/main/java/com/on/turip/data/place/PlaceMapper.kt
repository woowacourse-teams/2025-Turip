package com.on.turip.data.place

import com.on.turip.data.place.dto.FavoritePlacesResponse
import com.on.turip.domain.favorite.TuripPlace
import com.on.turip.domain.trip.Place

fun FavoritePlacesResponse.toDomain(): List<TuripPlace> =
    favoritePlaceResponses.map { favoritePlace ->
        TuripPlace(
            id = favoritePlace.id,
            order = favoritePlace.order,
            place =
                Place(
                    placeId = favoritePlace.placeResponse.id,
                    name = favoritePlace.placeResponse.name,
                    url = favoritePlace.placeResponse.url,
                    address = favoritePlace.placeResponse.address,
                    latitude = favoritePlace.placeResponse.latitude,
                    longitude = favoritePlace.placeResponse.longitude,
                    category = favoritePlace.placeResponse.categories.map { it.name },
                ),
        )
    }
