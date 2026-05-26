package com.on.turip.core.model

sealed interface SessionState {
    data object LoggedIn : SessionState
    data object Guest : SessionState
    data object Unknown : SessionState
}
