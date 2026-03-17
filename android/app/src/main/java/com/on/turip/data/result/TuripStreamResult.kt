package com.on.turip.data.result

import com.on.turip.core.result.ErrorType
import com.on.turip.domain.turip.TuripStreamEvent

sealed interface TuripStreamResult {
    data class Event(
        val event: TuripStreamEvent,
    ) : TuripStreamResult

    data class Reconnecting(
        val retryCount: Int,
    ) : TuripStreamResult

    sealed interface Fatal : TuripStreamResult {
        data object TokenExpired : Fatal

        data object Forbidden : Fatal

        data class ConnectionLost(
            val errorType: ErrorType,
        ) : Fatal
    }
}
