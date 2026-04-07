package com.on.turip.domain.session

sealed interface AuthStatus {
    data object Authenticated : AuthStatus

    data object UnAuthenticated : AuthStatus
}
