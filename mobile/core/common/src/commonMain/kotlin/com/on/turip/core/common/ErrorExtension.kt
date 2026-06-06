package com.on.turip.core.common

import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.network.dto.common.ErrorResponse

fun ErrorResponse?.toErrorType(): ErrorType {
    if (this == null) return ErrorType.Unknown
    return tag.trim().toErrorType()
}

/**
 * 서버 에러 tag 매핑
 */
private fun String.toErrorType(): ErrorType =
    when (this) {
        "ID_TOKEN_NOT_VALID" -> ErrorType.Auth.InvalidIdToken
        "REFRESH_TOKEN_NOT_FOUND" -> ErrorType.Auth.TokenNotFound
        "REFRESH_TOKEN_SIGNATURE_INVALID" -> ErrorType.Auth.InvalidTokenSignature
        "REFRESH_TOKEN_EXPIRED" -> ErrorType.Auth.TokenExpired
        "REFRESH_TOKEN_INVALID" -> ErrorType.Auth.InvalidToken
        "ACCESS_TOKEN_EXPIRED" -> ErrorType.Auth.TokenExpired
        "ACCESS_TOKEN_SIGNATURE_INVALID" -> ErrorType.Auth.InvalidTokenSignature
        "UNAUTHORIZED" -> ErrorType.Auth.UnAuthorized
        "FORBIDDEN" -> ErrorType.Auth.Forbidden
        "FOLDER_STREAM_FORBIDDEN" -> ErrorType.Auth.Forbidden
        "DEVICE_FID_REQUIRED" -> ErrorType.Device.FidRequired
        "CREATOR_NOT_FOUND" -> ErrorType.Creator.NotFound
        "CONTENT_NOT_FOUND" -> ErrorType.Content.NotFound
        "PLACE_NOT_FOUND" -> ErrorType.Place.NotFound
        "REGION_CATEGORY_INVALID" -> ErrorType.Region.InvalidCategory
        "FAVORITE_FOLDER_NOT_FOUND" -> ErrorType.Turip.NotFound
        "FAVORITE_FOLDER_NAME_CONFLICT" -> ErrorType.Turip.DuplicatedName
        "FAVORITE_FOLDER_NAME_BLANK" -> ErrorType.Turip.BlankName
        "FAVORITE_FOLDER_NAME_TOO_LONG" -> ErrorType.Turip.ExceededName
        "DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED" -> ErrorType.Turip.DefaultTuripRenameNotAllowed
        "FAVORITE_PLACE_NOT_FOUND" -> ErrorType.TuripPlace.NotFound
        "FAVORITE_PLACE_IN_FOLDER_CONFLICT" -> ErrorType.TuripPlace.DuplicatePlaceInTurip
        "FAVORITE_CONTENT_CONFLICT" -> ErrorType.Bookmark.DuplicateBookmarked
        else -> ErrorType.Unknown
    }
