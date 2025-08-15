package com.on.turip.data.common

import retrofit2.HttpException
import retrofit2.Response

@Suppress("UNCHECKED_CAST")
suspend inline fun <T> safeApiCall(apiCall: suspend () -> Response<T>): TuripCustomResult<T> =
    runCatching { apiCall() }
        .mapCatching { response: Response<T> ->
            TuripCustomResult.success(response.body() as T)
        }.getOrElse { error: Throwable ->
            when (error) {
                is HttpException -> TuripCustomResult.HttpError(error.code())
                else -> TuripCustomResult.NetworkError(error)
            }
        }
