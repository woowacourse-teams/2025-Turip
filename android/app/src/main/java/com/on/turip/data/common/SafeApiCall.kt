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
                TuripCustomResult.ParseError(
                    IllegalStateException("Response의 Body가 존재하지 않습니다."),
                )
            }
        } else {
            TuripCustomResult.HttpError(response.code())
        }
    } catch (e: HttpException) {
        TuripCustomResult.HttpError(e.code())
    } catch (e: Throwable) {
        TuripCustomResult.NetworkError(e)
    }
