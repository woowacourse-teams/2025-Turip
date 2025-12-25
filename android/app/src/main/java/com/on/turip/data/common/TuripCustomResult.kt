package com.on.turip.data.common

sealed class TuripCustomResult<out T> {
    data class Success<out T>(
        val value: T,
    ) : TuripCustomResult<T>()

    data class Failure(
        val errorType: ErrorType,
        val cause: Throwable? = null,
    ) : TuripCustomResult<Nothing>()
}
