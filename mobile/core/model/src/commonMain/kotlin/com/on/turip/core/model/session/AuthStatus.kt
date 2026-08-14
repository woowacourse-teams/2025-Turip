package com.on.turip.core.model.session

sealed interface AuthStatus {
    data object Authenticated : AuthStatus

    data object UnAuthenticated : AuthStatus
}
