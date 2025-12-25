package com.on.turip.data.common

/**
 * 성공일 경우
 */
inline fun <T> TuripCustomResult<T>.onSuccess(action: (value: T) -> Unit): TuripCustomResult<T> {
    if (this is TuripCustomResult.Success) action(value)
    return this
}

/**
 * 예외 발생한 경우
 */
inline fun <T> TuripCustomResult<T>.onFailure(action: (errorType: ErrorType) -> Unit): TuripCustomResult<T> {
    if (this is TuripCustomResult.Failure) action(errorType)
    return this
}

/**
 * 예외 발생한 경우(로깅용)
 */
inline fun <T> TuripCustomResult<T>.onFailureWithCause(action: (errorType: ErrorType, cause: Throwable?) -> Unit): TuripCustomResult<T> {
    if (this is TuripCustomResult.Failure) action(errorType, cause)
    return this
}

/**
 * 안전한 변환 지원
 */
inline fun <R, T> TuripCustomResult<T>.mapCatching(transform: (value: T) -> R): TuripCustomResult<R> =
    when (this) {
        is TuripCustomResult.Success -> {
            runCatching { transform(value) }.fold(
                onSuccess = { TuripCustomResult.Success(it) },
                onFailure = { TuripCustomResult.Failure(ErrorType.Unknown, cause = it) },
            )
        }

        is TuripCustomResult.Failure -> {
            this
        }
    }

/**
 * 변환
 */
inline fun <R, T> TuripCustomResult<T>.map(transform: (value: T) -> R): TuripCustomResult<R> =
    when (this) {
        is TuripCustomResult.Success -> TuripCustomResult.Success(transform(value))
        is TuripCustomResult.Failure -> this
    }

/**
 * 동일 타입 결과 반환
 */
inline fun <R, T> TuripCustomResult<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (errorType: ErrorType) -> R,
): R =
    when (this) {
        is TuripCustomResult.Success -> onSuccess(value)
        is TuripCustomResult.Failure -> onFailure(errorType)
    }

inline fun <T> TuripCustomResult<T>.getOrElse(onFailure: (errorType: ErrorType) -> T): T =
    when (this) {
        is TuripCustomResult.Success -> value
        is TuripCustomResult.Failure -> onFailure(errorType)
    }

fun <T> TuripCustomResult<T>.getOrNull(): T? =
    when (this) {
        is TuripCustomResult.Success -> value
        is TuripCustomResult.Failure -> null
    }
