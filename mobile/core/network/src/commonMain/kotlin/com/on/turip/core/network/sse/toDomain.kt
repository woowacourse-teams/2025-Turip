package com.on.turip.core.network.sse

import com.on.turip.core.model.bookmark.TuripPlace
import com.on.turip.core.model.trip.Place
import com.on.turip.core.model.turip.Turip
import com.on.turip.core.model.turip.TuripInvitationInformation
import com.on.turip.core.model.turip.TuripInvitationToken
import com.on.turip.core.model.turip.TuripMember
import com.on.turip.core.model.turip.TuripStreamEvent
import com.on.turip.core.data.dto.sse.TuripStreamConnectPayload
import com.on.turip.core.data.dto.turip.TuripByPlaceResponse
import com.on.turip.core.data.dto.turip.TuripCreationResponse
import com.on.turip.core.data.dto.turip.TuripInvitationInformationResponse
import com.on.turip.core.data.dto.turip.TuripInvitationTokenResponse
import com.on.turip.core.data.dto.turip.TuripMembersResponse
import com.on.turip.core.data.dto.turip.TuripPatchRequest
import com.on.turip.core.data.dto.turip.TuripPlacesResponse
import com.on.turip.core.data.dto.turip.TuripPostRequest
import com.on.turip.core.data.dto.turip.TuripResponse
import com.on.turip.core.data.dto.turip.TuripsByPlaceResponse
import com.on.turip.core.data.dto.turip.TuripsResponse
import com.on.turip.data.turip.stream.TuripStreamFolderUpdatePayload
import com.on.turip.data.turip.stream.TuripStreamHeartbeatPayload
import com.on.turip.data.turip.stream.TuripStreamMemberUpdatePayload

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

fun String.toPostRequestDto(): TuripPostRequest = TuripPostRequest(name = this)

fun String.toPatchRequestDto(): TuripPatchRequest = TuripPatchRequest(name = this)

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

fun TuripStreamConnectPayload.toDomain(id: String): TuripStreamEvent =
    TuripStreamEvent.Connect(
        id = id,
        turipId = turipId,
        timestamp = timestamp,
    )

fun TuripStreamFolderUpdatePayload.toDomain(id: String): TuripStreamEvent =
    TuripStreamEvent.FolderUpdate(
        id = id,
        turipId = turipId,
        action = action.toFolderAction(),
        timestamp = timestamp,
    )

fun TuripStreamMemberUpdatePayload.toDomain(id: String): TuripStreamEvent =
    TuripStreamEvent.MemberUpdate(
        id = id,
        turipId = turipId,
        action = action.toMemberAction(),
        memberCount = memberCount,
        members = members.map { TuripStreamEvent.Member(nickname = it.nickname) },
        timestamp = timestamp,
    )

fun TuripStreamHeartbeatPayload.toDomain(id: String): TuripStreamEvent =
    TuripStreamEvent.Heartbeat(
        id = id,
        timestamp = timestamp,
    )

private fun String.toFolderAction(): TuripStreamEvent.FolderAction =
    when (this) {
        "PLACE_REORDERED" -> TuripStreamEvent.FolderAction.PLACE_REORDERED
        "PLACE_ADDED" -> TuripStreamEvent.FolderAction.PLACE_ADDED
        "PLACE_DELETED" -> TuripStreamEvent.FolderAction.PLACE_DELETED
        "FOLDER_NAME_CHANGED" -> TuripStreamEvent.FolderAction.FOLDER_NAME_CHANGED
        "FOLDER_DELETED" -> TuripStreamEvent.FolderAction.FOLDER_DELETED
        "FOLDER_PLACE_CHANGED" -> TuripStreamEvent.FolderAction.FOLDER_PLACE_CHANGED
        else -> TuripStreamEvent.FolderAction.UNKNOWN
    }

private fun String.toMemberAction(): TuripStreamEvent.MemberAction =
    when (this) {
        "MEMBER_JOINED" -> TuripStreamEvent.MemberAction.MEMBER_JOINED
        "MEMBER_EXITED" -> TuripStreamEvent.MemberAction.MEMBER_EXITED
        else -> TuripStreamEvent.MemberAction.UNKNOWN
    }
