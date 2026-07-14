package com.on.turip.core.common

import com.on.turip.core.model.result.ErrorType

sealed class ApiException : Exception() {
    data class Error(
        val errorType: ErrorType,
    ) : ApiException()

    data object Auth : ApiException()

    data object Network : ApiException()
}
