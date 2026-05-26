package com.on.turip.core.common

import kotlinx.coroutines.CancellationException

suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiException) {
        val networkError = when (e) {
            is ApiException.Auth -> NetworkError.Auth.UnAuthorized
            is ApiException.Network -> NetworkError.Network
            is ApiException.Error -> e.networkError
        }
        Result.failure(NetworkException(networkError, e))
    } catch (e: Exception) {
        Result.failure(NetworkException(NetworkError.Unknown(e), e))
    }

class NetworkException(
    val networkError: NetworkError,
    cause: Throwable? = null,
) : Exception(cause)
