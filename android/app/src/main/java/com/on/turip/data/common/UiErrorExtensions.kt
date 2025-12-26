package com.on.turip.data.common

import com.on.turip.data.common.ErrorType.Auth
import com.on.turip.data.common.ErrorType.Content
import com.on.turip.data.common.ErrorType.Creator
import com.on.turip.data.common.ErrorType.FavoriteFolder
import com.on.turip.data.common.ErrorType.FavoritePlace
import com.on.turip.data.common.ErrorType.Place

fun ErrorType.toUiError(): UiError =
    when (this) {
        Auth.TokenExpired -> UiError.TokenExpired

        Auth.Forbidden -> UiError.PermissionDenied

        Creator.NotFound,
        Content.NotFound,
        Place.NotFound,
        FavoritePlace.NotFound,
        FavoriteFolder.NotFound,
        -> UiError.NotFound

        FavoriteFolder.DuplicatedName,
        FavoritePlace.DuplicatePlaceInFolder,
        ErrorType.FavoriteContent.DuplicateContent,
        -> UiError.Duplicated

        FavoriteFolder.BlankName,
        FavoriteFolder.ExceededName,
        FavoriteFolder.DefaultFolderRenameNotAllowed,
        -> UiError.InValid

        ErrorType.Network -> UiError.Network

        else -> UiError.Unknown
    }
