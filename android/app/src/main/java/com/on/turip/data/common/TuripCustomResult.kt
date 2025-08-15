package com.on.turip.data.common

import com.on.turip.domain.ErrorEvent
import timber.log.Timber

sealed class TuripCustomResult<out T> {
    data class Success<out T>(
        val data: T,
    ) : TuripCustomResult<T>()

    data class HttpError(
        private val statusCode: Int,
    ) : TuripCustomResult<Nothing>() {
        init {
            when (statusCode) {
                403 -> Timber.e("$statusCode : 사용자의 권한이 없습니다.")
                404 -> Timber.e("$statusCode : 찾을 수 없음")
                409 -> Timber.e("$statusCode : 폴더명이 중복입니다.")
                500 -> Timber.e("$statusCode : 서버에서 예기치 않은 문제가 발생하였습니다.")
                else -> Timber.e("찾을 수 없는 코드: $statusCode")
            }
        }

        val error: ServerError
            get() =
                when (statusCode) {
                    403 -> ServerError.USER_NOT_HAVE_PERMISSION
                    404 -> ServerError.NOT_FOUND
                    409 -> ServerError.DUPLICATION_FOLDER
                    500 -> ServerError.UNEXPECTED_PROBLEM
                    else -> ServerError.UNKNOWN
                }
    }

    data class ParseError(
        val exception: Throwable,
    ) : TuripCustomResult<Nothing>() {
        init {
            Timber.e("파싱 에러: ${exception.message}")
        }
    }

    data class NetworkError(
        val exception: Throwable,
    ) : TuripCustomResult<Nothing>() {
        init {
            Timber.e("네트워크 에러: ${exception.message}")
        }
    }

    companion object {
        fun <T> success(value: T): TuripCustomResult<T> = Success(value)
    }
}

inline fun <T> TuripCustomResult<T>.onSuccess(action: (value: T) -> Unit): TuripCustomResult<T> {
    if (this is TuripCustomResult.Success) action(data)
    return this
}

inline fun <T> TuripCustomResult<T>.onFailure(action: (ErrorEvent) -> Unit): TuripCustomResult<T> {
    when (this) {
        is TuripCustomResult.ParseError -> ErrorEvent.PARSER_ERROR
        is TuripCustomResult.NetworkError -> ErrorEvent.NETWORK_ERROR
        is TuripCustomResult.HttpError ->
            action(
                when (error) {
                    ServerError.USER_NOT_HAVE_PERMISSION -> ErrorEvent.USER_NOT_HAVE_PERMISSION
                    ServerError.NOT_FOUND -> ErrorEvent.UNEXPECTED_PROBLEM
                    ServerError.DUPLICATION_FOLDER -> ErrorEvent.DUPLICATION_FOLDER
                    ServerError.UNEXPECTED_PROBLEM -> ErrorEvent.UNEXPECTED_PROBLEM
                    ServerError.UNKNOWN -> ErrorEvent.UNEXPECTED_PROBLEM
                },
            )

        is TuripCustomResult.Success -> this
    }
    return this
}

inline fun <R, T> TuripCustomResult<T>.mapCatching(transform: (value: T) -> R): TuripCustomResult<R> =
    when (this) {
        is TuripCustomResult.Success ->
            runCatching {
                transform(data)
            }.fold(
                onSuccess = { TuripCustomResult.Success(it) },
                onFailure = { e -> TuripCustomResult.ParseError(e) },
            )

        is TuripCustomResult.ParseError -> this
        is TuripCustomResult.NetworkError -> this
        is TuripCustomResult.HttpError -> this
    }
