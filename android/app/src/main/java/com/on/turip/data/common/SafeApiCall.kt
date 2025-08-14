package com.on.turip.data.common

import retrofit2.HttpException
import retrofit2.Response

@Suppress("UNCHECKED_CAST")
suspend inline fun <T> safeApiCall(apiCall: suspend () -> Response<T>): TuripCustomResult<T> =
    runCatching { apiCall() }
        .mapCatching { response: Response<T> ->
            TuripCustomResult.success(response.body() as T)
        }.getOrElse { e: Throwable ->
            when (e) {
                is HttpException -> TuripCustomResult.HttpError(e.code())
                else -> TuripCustomResult.NetworkError(e)
            }
        }
