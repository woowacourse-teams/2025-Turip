package com.on.turip.data.common

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): TuripCustomResult<T> {
    try {
        val response: Response<T> = apiCall()
        if (response.isSuccessful) {
            @Suppress("UNCHECKED_CAST")
            return TuripCustomResult.Success(response.body() as T)
        }

        return TuripCustomResult.Failure(response.toErrorType(), HttpException(response))
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        return when (e) {
            is IOException -> TuripCustomResult.Failure(ErrorType.Network, e)
            else -> TuripCustomResult.Failure(ErrorType.Unknown, e)
        }
    }
}

private fun <T> Response<T>.toErrorType(): ErrorType =
    runCatching {
        this
            .errorBody()
            ?.string()
            ?.let { Json.decodeFromString<ErrorResponse>(it) }
            ?.toErrorType() ?: ErrorType.Unknown
    }.getOrElse {
        ErrorType.Unknown
    }
