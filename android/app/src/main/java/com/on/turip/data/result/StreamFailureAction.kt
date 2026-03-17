package com.on.turip.data.result

enum class StreamFailureAction {
    Retry,
    Stop,
    FatalTokenExpired,
    FatalForbidden,
}
