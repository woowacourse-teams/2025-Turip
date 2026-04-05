package com.on.turip.ui.common.error

import androidx.compose.runtime.Immutable

/**
 * presentation/ui layer
 */
@Immutable
sealed interface UiError {
    sealed interface Global : UiError {
        /**
         * 토큰 갱신 실패 시, 로그인으로 이동하게 된다는 정보를 전달
         */
        data object TokenExpired : Global

        /**
         * 인터넷 연결에 문제가 있다는 정보를 전달
         */
        data object Network : Global

        /**
         * 서버 또는 클라이언트가 정보 전달하는 과정에서 문제 발생
         * 서버에 문제가 있음을 알리는 정보를 전달
         */
        data object Server : Global
    }

    sealed interface Feature : UiError {
        /**
         * 권한이 없는 화면에 접근하려고 한다는 정보를 전달
         */
        data object PermissionDenied : Feature

        /**
         * 접근하려는 데이터의 정보를 찾을 수 없다는 정보를 전달
         */
        data object NotFound : Feature

        /**
         * 중복된 데이터가 존재한다는 정보를 전달
         */
        data object Duplicated : Feature

        /**
         * 유효하지 않는 데이터 형태라는 정보를 전달
         * 비어있는 데이터, 허용 범위를 초과하는 데이터, 변경 불가능한 데이터 조작 시도 등
         */
        data object InValid : Feature

        /**
         * 사용자가 직접 작업을 취소했다는 정보를 전달
         */
        data object Cancelled : Feature
    }
}
