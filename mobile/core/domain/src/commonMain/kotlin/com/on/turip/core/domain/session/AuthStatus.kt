package com.on.turip.core.domain.session

sealed interface AuthStatus {
    data object Authenticated : AuthStatus

    data object UnAuthenticated : AuthStatus
}
