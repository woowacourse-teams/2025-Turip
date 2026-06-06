package com.on.turip.core.data.repository

import com.on.turip.core.data.dto.turip.TuripByPlaceResponse
import com.on.turip.core.data.dto.turip.TuripCreationResponse
import com.on.turip.core.data.dto.turip.TuripInvitationInformationResponse
import com.on.turip.core.data.dto.turip.TuripInvitationTokenResponse
import com.on.turip.core.data.dto.turip.TuripMembersResponse
import com.on.turip.core.data.dto.turip.TuripPlacesResponse
import com.on.turip.core.data.dto.turip.TuripResponse
import com.on.turip.core.data.dto.turip.TuripsByPlaceResponse
import com.on.turip.core.data.dto.turip.TuripsResponse
import com.on.turip.core.model.bookmark.TuripPlace
import com.on.turip.core.model.trip.Place
import com.on.turip.core.model.turip.Turip
import com.on.turip.core.model.turip.TuripInvitationInformation
import com.on.turip.core.model.turip.TuripInvitationToken
import com.on.turip.core.model.turip.TuripMember

fun TuripsResponse.toDomain(): List<Turip> = turipsResponse.map { it.toDomain() }

fun TuripMembersResponse.toDomain(): List<TuripMember> = members.map { TuripMember(it.nickname) }

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

fun TuripsByPlaceResponse.toDomain(): List<Turip> = turips.map { it.toDomain() }

fun TuripByPlaceResponse.toDomain(): Turip =
    Turip(
        id = id,
        name = name,
        isDefault = isDefault,
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
            place = Place(
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
