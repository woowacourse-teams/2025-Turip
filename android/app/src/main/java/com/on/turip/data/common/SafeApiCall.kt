package com.on.turip.data.common

import com.on.turip.data.common.data.ErrorResponse
import com.on.turip.data.common.data.toErrorType
import com.on.turip.data.common.domain.ErrorType
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): TuripCustomResult<T> {
    try {
        val response: Response<T> = apiCall()
        if (response.isSuccessful) {
            @Suppress("UNCHECKED_CAST")
            return TuripCustomResult.Success(response.body() as T)
        }

        val errorResponse: ErrorResponse? =
            response.errorBody()?.string()?.let { Json.decodeFromString<ErrorResponse>(it) }
        return TuripCustomResult.Failure(errorResponse.toErrorType())
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        return when (e) {
            is IOException -> TuripCustomResult.Failure(ErrorType.Network)
            else -> TuripCustomResult.Failure(ErrorType.Unknown)
        }
    }
}
