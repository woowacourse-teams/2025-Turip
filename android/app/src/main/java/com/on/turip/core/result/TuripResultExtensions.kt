package com.on.turip.core.result

/**
 * 성공일 경우
 */
inline fun <T> TuripResult<T>.onSuccess(action: (value: T) -> Unit): TuripResult<T> {
    if (this is TuripResult.Success) action(value)
    return this
}

/**
 * 예외 발생한 경우
 */
inline fun <T> TuripResult<T>.onFailure(action: (errorType: ErrorType) -> Unit): TuripResult<T> {
    if (this is TuripResult.Failure) action(errorType)
    return this
}

/**
 * 예외 발생한 경우(로깅용)
 */
inline fun <T> TuripResult<T>.onFailureWithCause(action: (errorType: ErrorType, cause: Throwable?) -> Unit): TuripResult<T> {
    if (this is TuripResult.Failure) action(errorType, cause)
    return this
}

/**
 * 안전한 변환 지원
 */
inline fun <R, T> TuripResult<T>.mapCatching(transform: (value: T) -> R): TuripResult<R> =
    when (this) {
        is TuripResult.Success -> {
            runCatching { transform(value) }.fold(
                onSuccess = { TuripResult.Success(it) },
                onFailure = { TuripResult.Failure(ErrorType.Unknown, cause = it) },
            )
        }

        is TuripResult.Failure -> this
    }

/**
 * 변환
 */
inline fun <R, T> TuripResult<T>.map(transform: (value: T) -> R): TuripResult<R> =
    when (this) {
        is TuripResult.Success -> TuripResult.Success(transform(value))
        is TuripResult.Failure -> this
    }

/**
 * 동일 타입 결과 반환
 */
inline fun <R, T> TuripResult<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (errorType: ErrorType) -> R,
): R =
    when (this) {
        is TuripResult.Success -> onSuccess(value)
        is TuripResult.Failure -> onFailure(errorType)
    }

inline fun <T> TuripResult<T>.getOrElse(onFailure: (errorType: ErrorType) -> T): T =
    when (this) {
        is TuripResult.Success -> value
        is TuripResult.Failure -> onFailure(errorType)
    }

fun <T> TuripResult<T>.getOrNull(): T? =
    when (this) {
        is TuripResult.Success -> value
        is TuripResult.Failure -> null
    }
