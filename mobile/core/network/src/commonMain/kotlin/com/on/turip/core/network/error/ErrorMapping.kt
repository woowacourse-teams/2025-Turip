package com.on.turip.core.network.error

import com.on.turip.core.common.NetworkError

private object ErrorTag {
    const val INVALID_ID_TOKEN = "INVALID_ID_TOKEN"
    const val INVALID_TOKEN_SIGNATURE = "INVALID_TOKEN_SIGNATURE"
    const val INVALID_TOKEN = "INVALID_TOKEN"
    const val TOKEN_EXPIRED = "TOKEN_EXPIRED"
    const val TOKEN_NOT_FOUND = "TOKEN_NOT_FOUND"
    const val UNAUTHORIZED = "UNAUTHORIZED"
    const val FORBIDDEN = "FORBIDDEN"
    const val FID_REQUIRED = "FID_REQUIRED"
    const val CREATOR_NOT_FOUND = "CREATOR_NOT_FOUND"
    const val CONTENT_NOT_FOUND = "CONTENT_NOT_FOUND"
    const val PLACE_NOT_FOUND = "PLACE_NOT_FOUND"
    const val INVALID_REGION_CATEGORY = "INVALID_REGION_CATEGORY"
    const val TURIP_NOT_FOUND = "TURIP_NOT_FOUND"
    const val DUPLICATED_TURIP_NAME = "DUPLICATED_TURIP_NAME"
    const val BLANK_TURIP_NAME = "BLANK_TURIP_NAME"
    const val EXCEEDED_TURIP_NAME = "EXCEEDED_TURIP_NAME"
    const val DEFAULT_TURIP_RENAME_NOT_ALLOWED = "DEFAULT_TURIP_RENAME_NOT_ALLOWED"
    const val TURIP_PLACE_NOT_FOUND = "TURIP_PLACE_NOT_FOUND"
    const val DUPLICATE_PLACE_IN_TURIP = "DUPLICATE_PLACE_IN_TURIP"
    const val DUPLICATE_BOOKMARKED = "DUPLICATE_BOOKMARKED"
}

fun String.toNetworkError(): NetworkError = when (this) {
    ErrorTag.INVALID_ID_TOKEN -> NetworkError.Auth.InvalidIdToken
    ErrorTag.INVALID_TOKEN_SIGNATURE -> NetworkError.Auth.InvalidTokenSignature
    ErrorTag.INVALID_TOKEN -> NetworkError.Auth.InvalidToken
    ErrorTag.TOKEN_EXPIRED -> NetworkError.Auth.TokenExpired
    ErrorTag.TOKEN_NOT_FOUND -> NetworkError.Auth.TokenNotFound
    ErrorTag.UNAUTHORIZED -> NetworkError.Auth.UnAuthorized
    ErrorTag.FORBIDDEN -> NetworkError.Auth.Forbidden
    ErrorTag.FID_REQUIRED -> NetworkError.Device.FidRequired
    ErrorTag.CREATOR_NOT_FOUND -> NetworkError.Creator.NotFound
    ErrorTag.CONTENT_NOT_FOUND -> NetworkError.Content.NotFound
    ErrorTag.PLACE_NOT_FOUND -> NetworkError.Place.NotFound
    ErrorTag.INVALID_REGION_CATEGORY -> NetworkError.Region.InvalidCategory
    ErrorTag.TURIP_NOT_FOUND -> NetworkError.Turip.NotFound
    ErrorTag.DUPLICATED_TURIP_NAME -> NetworkError.Turip.DuplicatedName
    ErrorTag.BLANK_TURIP_NAME -> NetworkError.Turip.BlankName
    ErrorTag.EXCEEDED_TURIP_NAME -> NetworkError.Turip.ExceededName
    ErrorTag.DEFAULT_TURIP_RENAME_NOT_ALLOWED -> NetworkError.Turip.DefaultTuripRenameNotAllowed
    ErrorTag.TURIP_PLACE_NOT_FOUND -> NetworkError.TuripPlace.NotFound
    ErrorTag.DUPLICATE_PLACE_IN_TURIP -> NetworkError.TuripPlace.DuplicatePlaceInTurip
    ErrorTag.DUPLICATE_BOOKMARKED -> NetworkError.Bookmark.DuplicateBookmarked
    else -> NetworkError.Unknown()
}
