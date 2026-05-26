package com.on.turip.core.data.mapper

import com.on.turip.core.model.Member
import com.on.turip.core.model.Place
import com.on.turip.core.model.Turip
import com.on.turip.core.model.TuripInvitationInformation
import com.on.turip.core.model.TuripPlace
import com.on.turip.core.model.TuripStatus
import com.on.turip.core.network.dto.turip.InvitationInformationResponse
import com.on.turip.core.network.dto.turip.MemberResponse
import com.on.turip.core.network.dto.turip.TuripPlaceResponse
import com.on.turip.core.network.dto.turip.TuripResponse
import com.on.turip.core.network.dto.turip.TuripStatusResponse

internal fun TuripResponse.toDomain() = Turip(
    id = id,
    name = name,
    isDefault = isDefault,
    placeCount = placeCount,
    memberCount = memberCount,
    isShared = isShared,
    hasIncludePlace = hasIncludePlace,
)

internal fun MemberResponse.toDomain() = Member(
    id = id,
    nickname = nickname,
    profileImageUrl = profileImageUrl,
)

internal fun TuripPlaceResponse.toDomain() = TuripPlace(
    turipId = turipId,
    place = Place(
        id = placeId,
        name = name,
        category = category,
        latitude = latitude,
        longitude = longitude,
    ),
    order = order,
)

internal fun TuripStatusResponse.toDomain() = TuripStatus(
    turipId = turipId,
    turipName = turipName,
    isIncluded = isIncluded,
)

internal fun InvitationInformationResponse.toDomain() = TuripInvitationInformation(
    turipId = turipId,
    turipName = turipName,
    memberCount = memberCount,
    isAlreadyJoined = isAlreadyJoined,
)