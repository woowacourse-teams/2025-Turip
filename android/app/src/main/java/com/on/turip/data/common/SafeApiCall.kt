package com.on.turip.data.common

import retrofit2.HttpException
import retrofit2.Response

suspend inline fun <T> safeApiCall(apiCall: suspend () -> Response<T>): TuripCustomResult<T> =
    runCatching { apiCall() }
        .mapCatching { response: Response<T> ->
            if (response.isSuccessful) {
                val body = response.body() ?: throw IllegalStateException("서버에 body값이 현재 null입니다.")
                TuripCustomResult.Success(body)
            } else {
                TuripCustomResult.HttpError(response.code())
            }
        }.getOrElse { e: Throwable ->
            when (e) {
                is HttpException -> TuripCustomResult.HttpError(e.code())
                else -> TuripCustomResult.NetworkError(e)
            }
        }
