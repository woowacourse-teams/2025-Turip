package com.on.turip.data.result

import com.on.turip.core.result.ErrorType

sealed class ApiException : Exception() {
    data class Error(
        val errorType: ErrorType,
    ) : ApiException()

    data object Auth : ApiException()

    data object Network : ApiException()
}
