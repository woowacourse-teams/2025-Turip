package com.on.turip.core.result

sealed class TuripResult<out T> {
    data class Success<out T>(
        val value: T,
    ) : TuripResult<T>()

    data class Failure(
        val errorType: ErrorType,
        val cause: Throwable,
    ) : TuripResult<Nothing>()
}
