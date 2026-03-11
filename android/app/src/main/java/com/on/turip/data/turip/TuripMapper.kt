package com.on.turip.data.turip

import com.on.turip.data.turip.dto.TuripByPlaceResponse
import com.on.turip.data.turip.dto.TuripCreationResponse
import com.on.turip.data.turip.dto.TuripInvitationInformationResponse
import com.on.turip.data.turip.dto.TuripInvitationTokenResponse
import com.on.turip.data.turip.dto.TuripPatchRequest
import com.on.turip.data.turip.dto.TuripPlacesResponse
import com.on.turip.data.turip.dto.TuripPostRequest
import com.on.turip.data.turip.dto.TuripResponse
import com.on.turip.data.turip.dto.TuripsByPlaceResponse
import com.on.turip.data.turip.dto.TuripsResponse
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.trip.Place
import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.TuripInvitationInformation
import com.on.turip.domain.turip.TuripInvitationToken

fun TuripsResponse.toDomain(): List<Turip> = turipsResponse.map { it.toDomain() }

fun TuripResponse.toDomain(): Turip =
    Turip(
        id = id,
        name = name,
        isDefault = isDefault,
        placeCount = placeCount,
        memberCount = memberCount,
        isShared = isShared,
    )

fun TuripCreationResponse.toDomain(): Turip =
    Turip(
        id = id,
        name = name,
        isDefault = isDefault,
        placeCount = 0,
        memberCount = 0,
        isShared = false,
    )

fun String.toPostRequestDto(): TuripPostRequest = TuripPostRequest(name = this)

fun String.toPatchRequestDto(): TuripPatchRequest = TuripPatchRequest(name = this)

fun TuripsByPlaceResponse.toDomain(): List<Turip> = turips.map { it.toDomain() }

fun TuripByPlaceResponse.toDomain(): Turip =
    Turip(
        id = id,
        name = name,
        isDefault = false,
        placeCount = 0,
        memberCount = 0,
        isShared = false,
        hasIncludePlace = isTuripPlace,
    )

fun TuripPlacesResponse.toDomain(): List<TuripPlace> =
    turipPlaceResponses.map { favoritePlace ->
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

fun TuripInvitationTokenResponse.toDomain(): TuripInvitationToken = TuripInvitationToken(value = invitationToken)

fun TuripInvitationInformationResponse.toDomain(): TuripInvitationInformation =
    TuripInvitationInformation(
        turipId = turipId,
        alreadyJoined = alreadyJoined,
    )
