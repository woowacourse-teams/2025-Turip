package com.on.turip.data.common

/**
 * domain layer
 */
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
     * 찜 폴더에 대한 에러 타입
     */
    sealed interface FavoriteFolder : ErrorType {
        data object NotFound : FavoriteFolder

        data object DuplicatedName : FavoriteFolder

        data object BlankName : FavoriteFolder

        data object ExceededName : FavoriteFolder

        data object DefaultFolderRenameNotAllowed : FavoriteFolder
    }

    /**
     * 찜 장소에 대한 에러 타입
     */
    sealed interface FavoritePlace : ErrorType {
        data object NotFound : FavoritePlace

        data object DuplicatePlaceInFolder : FavoritePlace
    }

    /**
     * 찜 컨텐츠에 대한 에러 타입
     */
    sealed interface FavoriteContent : ErrorType {
        data object DuplicateContent : FavoriteContent
    }

    /**
     * 로컬 데이터베이스에 대한 에러 타입
     */
    sealed interface Local : ErrorType {
        data object Unknown : Local
    }

    /**
     * 네트워크 에러에 대한 에러 타입
     */
    data object Network : ErrorType

    /**
     * 커스텀 예외 밖 모든 에러 타입
     */
    data object Unknown : ErrorType
}
