package com.on.turip.core.model.result

sealed interface ErrorType {
    /**
     * 401, 403
     * 권한 관련 에러 타입
     */
    sealed interface Auth : ErrorType {
        data object InvalidIdToken : Auth

        data object InvalidTokenSignature : Auth

        data object InvalidToken : Auth

        data object TokenExpired : Auth

        data object TokenNotFound : Auth

        data object UnAuthorized : Auth

        data object Forbidden : Auth
    }

    /**
     * 기기에 대한 에러 타입
     */
    sealed interface Device : ErrorType {
        data object FidRequired : Device
    }

    /**
     * 크리에이터에 대한 에러 타입
     */
    sealed interface Creator : ErrorType {
        data object NotFound : Creator
    }

    /**
     * 컨텐츠에 대한 에러 타입
     */
    sealed interface Content : ErrorType {
        data object NotFound : Content
    }

    /**
     * 장소에 대한 에러 타입
     */
    sealed interface Place : ErrorType {
        data object NotFound : Place
    }

    /**
     * 지역에 대한 에러 타입
     */
    sealed interface Region : ErrorType {
        data object InvalidCategory : Region
    }

    /**
     * 튜립에 대한 에러 타입
     */
    sealed interface Turip : ErrorType {
        data object NotFound : Turip

        data object DuplicatedName : Turip

        data object BlankName : Turip

        data object ExceededName : Turip

        data object DefaultTuripRenameNotAllowed : Turip
    }

    /**
     * 튜립된 장소에 대한 에러 타입
     */
    sealed interface TuripPlace : ErrorType {
        data object NotFound : TuripPlace

        data object DuplicatePlaceInTurip : TuripPlace
    }

    /**
     * 북마크에 대한 에러 타입
     */
    sealed interface Bookmark : ErrorType {
        data object DuplicateBookmarked : Bookmark
    }

    /**
     * FCM 토큰에 대한 에러 타입
     */
    sealed interface FcmToken : ErrorType {
        data object Blank : FcmToken

        data object NotificationEnabledRequired : FcmToken

        data object NotFound : FcmToken
    }

    /**
     * 네트워크 에러에 대한 에러 타입
     */
    data object Network : ErrorType

    /**
     * 사용자가 직접 작업을 취소한 에러 타입
     */
    data object Cancelled : ErrorType

    /**
     * 커스텀 예외 밖 모든 에러 타입
     */
    data object Unknown : ErrorType
}
