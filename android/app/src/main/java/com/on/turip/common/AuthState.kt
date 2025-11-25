package com.on.turip.common

object AuthState {
    var type: UserType = UserType.GUEST
        private set

    fun change(type: UserType) {
        this.type = type
    }
}
