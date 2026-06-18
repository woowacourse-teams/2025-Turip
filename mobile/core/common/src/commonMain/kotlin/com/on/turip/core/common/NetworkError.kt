package com.on.turip.core.common

sealed interface NetworkError {
    sealed interface Auth : NetworkError {
        data object InvalidIdToken : Auth

        data object InvalidTokenSignature : Auth

        data object InvalidToken : Auth

        data object TokenExpired : Auth

        data object TokenNotFound : Auth

        data object UnAuthorized : Auth

        data object Forbidden : Auth
    }

    sealed interface Device : NetworkError {
        data object FidRequired : Device
    }

    sealed interface Creator : NetworkError {
        data object NotFound : Creator
    }

    sealed interface Content : NetworkError {
        data object NotFound : Content
    }

    sealed interface Place : NetworkError {
        data object NotFound : Place
    }

    sealed interface Region : NetworkError {
        data object InvalidCategory : Region
    }

    sealed interface Turip : NetworkError {
        data object NotFound : Turip

        data object DuplicatedName : Turip

        data object BlankName : Turip

        data object ExceededName : Turip

        data object DefaultTuripRenameNotAllowed : Turip
    }

    sealed interface TuripPlace : NetworkError {
        data object NotFound : TuripPlace

        data object DuplicatePlaceInTurip : TuripPlace
    }

    sealed interface Bookmark : NetworkError {
        data object DuplicateBookmarked : Bookmark
    }

    data object Network : NetworkError

    data object Cancelled : NetworkError

    data class Unknown(
        val cause: Throwable? = null,
    ) : NetworkError
}
