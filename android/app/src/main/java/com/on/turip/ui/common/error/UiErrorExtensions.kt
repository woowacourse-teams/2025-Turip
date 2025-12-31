package com.on.turip.ui.common.error

import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.ErrorType.Auth
import com.on.turip.core.result.ErrorType.Content
import com.on.turip.core.result.ErrorType.Creator
import com.on.turip.core.result.ErrorType.FavoriteFolder
import com.on.turip.core.result.ErrorType.FavoritePlace
import com.on.turip.core.result.ErrorType.Place

fun ErrorType.toUiError(): UiError =
    when (this) {
        Auth.TokenExpired -> UiError.Global.TokenExpired

        Auth.Forbidden -> UiError.Feature.PermissionDenied

        Creator.NotFound,
        Content.NotFound,
        Place.NotFound,
        FavoritePlace.NotFound,
        FavoriteFolder.NotFound,
            -> UiError.Feature.NotFound

        FavoriteFolder.DuplicatedName,
        FavoritePlace.DuplicatePlaceInFolder,
        ErrorType.FavoriteContent.DuplicateContent,
            -> UiError.Feature.Duplicated

        FavoriteFolder.BlankName,
        FavoriteFolder.ExceededName,
        FavoriteFolder.DefaultFolderRenameNotAllowed,
            -> UiError.Feature.InValid

        ErrorType.Network -> UiError.Global.Network

        else -> UiError.Global.Server
    }
