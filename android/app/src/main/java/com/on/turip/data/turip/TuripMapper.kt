package com.on.turip.data.turip

import com.on.turip.data.turip.dto.TuripByPlaceResponse
import com.on.turip.data.turip.dto.TuripCreationResponse
import com.on.turip.data.turip.dto.TuripPatchRequest
import com.on.turip.data.turip.dto.TuripPostRequest
import com.on.turip.data.turip.dto.TuripPostResponse
import com.on.turip.data.turip.dto.TuripsByPlaceResponse
import com.on.turip.data.turip.dto.TuripsResponse
import com.on.turip.domain.folder.Turip

fun TuripsResponse.toDomain(): List<Turip> = turipsResponse.map { it.toDomain() }

fun TuripPostResponse.toDomain(): Turip =
    Turip(
        id = id,
        name = name,
        isDefault = isDefault,
        placeCount = placeCount,
    )

fun TuripCreationResponse.toDomain(): Turip =
    Turip(
        id = id,
        name = name,
        isDefault = isDefault,
        placeCount = 0,
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
        hasIncludePlace = isTuripPlace,
    )
