package com.on.turip.data.common

import retrofit2.HttpException
import retrofit2.Response

@Suppress("UNCHECKED_CAST")
suspend inline fun <T> safeApiCall(apiCall: suspend () -> Response<T>): TuripCustomResult<T> =
    try {
        val response: Response<T> = apiCall()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                TuripCustomResult.Success(body)
            } else {
                if (response.code() == 204) {
                    TuripCustomResult.Success(Unit as T)
                } else {
                    TuripCustomResult.ParseError(
                        IllegalStateException("예상치 못한 빈 응답입니다."),
                    )
                }
            }
        } else {
            TuripCustomResult.HttpError(response.code())
        }
    } catch (e: HttpException) {
        TuripCustomResult.HttpError(e.code())
    } catch (e: Throwable) {
        TuripCustomResult.NetworkError(e)
    }
