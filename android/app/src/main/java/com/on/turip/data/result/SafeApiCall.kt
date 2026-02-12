package com.on.turip.data.result

import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.TuripResult
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): TuripResult<T> =
    try {
        TuripResult.Success(value = apiCall())
    } catch (e: Exception) {
        if (e is CancellationException) throw e

        val errorType: ErrorType =
            when (e) {
                is ClientRequestException -> e.response.toErrorType()
                is ServerResponseException -> e.response.toErrorType()
                is IOException -> ErrorType.Network
                else -> ErrorType.Unknown
            }

        TuripResult.Failure(errorType, cause = e)
    }

private suspend fun HttpResponse.toErrorType(): ErrorType =
    runCatching {
        this.body<ErrorResponse>().toErrorType()
    }.getOrElse {
        ErrorType.Unknown
    }
