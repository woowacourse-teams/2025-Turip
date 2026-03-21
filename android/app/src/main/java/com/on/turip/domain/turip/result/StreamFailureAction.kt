package com.on.turip.domain.turip.result

enum class StreamFailureAction {
    Retry,
    Stop,
    FatalTokenExpired,
    FatalForbidden,
}
