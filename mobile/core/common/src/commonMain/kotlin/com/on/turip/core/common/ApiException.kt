package com.on.turip.core.common

sealed class ApiException(cause: Throwable? = null) : Exception(cause) {
    data object Auth : ApiException()
    data object Network : ApiException()
    data class Error(val networkError: NetworkError) : ApiException()
}
