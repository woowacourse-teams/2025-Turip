package com.on.turip.core.ui.error

import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.ErrorType.Auth
import com.on.turip.core.model.result.ErrorType.Content
import com.on.turip.core.model.result.ErrorType.Creator
import com.on.turip.core.model.result.ErrorType.Place
import com.on.turip.core.model.result.ErrorType.Turip
import com.on.turip.core.model.result.ErrorType.TuripPlace

fun ErrorType.toUiError(): UiError =
    when (this) {
        Auth.TokenExpired, Auth.UnAuthorized -> UiError.Global.TokenExpired

        Auth.Forbidden -> UiError.Feature.PermissionDenied

        Creator.NotFound,
        Content.NotFound,
        Place.NotFound,
        TuripPlace.NotFound,
        Turip.NotFound,
        -> UiError.Feature.NotFound

        Turip.DuplicatedName,
        TuripPlace.DuplicatePlaceInTurip,
        ErrorType.Bookmark.DuplicateBookmarked,
        -> UiError.Feature.Duplicated

        Turip.BlankName,
        Turip.ExceededName,
        Turip.DefaultTuripRenameNotAllowed,
        -> UiError.Feature.InValid

        ErrorType.Network -> UiError.Global.Network

        ErrorType.Cancelled -> UiError.Feature.Cancelled

        else -> UiError.Global.Server
    }
