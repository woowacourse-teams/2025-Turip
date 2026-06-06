package com.on.turip.core.common

import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.dto.common.ErrorResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): TuripResult<T> =
    try {
        TuripResult.Success(value = apiCall())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiException) {
        when (e) {
            ApiException.Network -> TuripResult.Failure(ErrorType.Network, e)
            ApiException.Auth -> TuripResult.Failure(ErrorType.Auth.UnAuthorized, e)
            is ApiException.Error -> TuripResult.Failure(e.errorType, e)
        }
    } catch (e: Exception) {
        TuripResult.Failure(e.toErrorType(), e)
    }

suspend fun Throwable.toErrorType(): ErrorType =
    when (this) {
        is CancellationException -> throw this
        is ClientRequestException -> response.toErrorType()
        is ServerResponseException -> response.toErrorType()
        is IOException -> ErrorType.Network
        else -> ErrorType.Unknown
    }

private suspend fun HttpResponse.toErrorType(): ErrorType =
    runCatching {
        this.body<ErrorResponse>().toErrorType()
    }.getOrElse {
        ErrorType.Unknown
    }
