package com.on.turip.data.common

/**
 * data layer
 */
fun ErrorResponse?.toErrorType(): ErrorType {
    if (this == null) return ErrorType.Unknown
    return this.tag.trim().toErrorType()
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
        "DEVICE_FID_REQUIRED" -> ErrorType.Device.FidRequired
        "CREATOR_NOT_FOUND" -> ErrorType.Creator.NotFound
        "CONTENT_NOT_FOUND" -> ErrorType.Content.NotFound
        "PLACE_NOT_FOUND" -> ErrorType.Place.NotFound
        "REGION_CATEGORY_INVALID" -> ErrorType.Region.InvalidCategory
        "FAVORITE_FOLDER_NOT_FOUND" -> ErrorType.FavoriteFolder.NotFound
        "FAVORITE_FOLDER_NAME_CONFLICT" -> ErrorType.FavoriteFolder.DuplicatedName
        "FAVORITE_FOLDER_NAME_BLANK" -> ErrorType.FavoriteFolder.BlankName
        "FAVORITE_FOLDER_NAME_TOO_LONG" -> ErrorType.FavoriteFolder.ExceededName
        "DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED" -> ErrorType.FavoriteFolder.DefaultFolderRenameNotAllowed
        "FAVORITE_PLACE_NOT_FOUND" -> ErrorType.FavoritePlace.NotFound
        "FAVORITE_PLACE_IN_FOLDER_CONFLICT" -> ErrorType.FavoritePlace.DuplicatePlaceInFolder
        "FAVORITE_CONTENT_CONFLICT" -> ErrorType.FavoriteContent.DuplicateContent
        else -> ErrorType.Unknown
    }
