package com.on.turip.common

object AuthState {
    var type: UserType = UserType.NONE
        private set

    fun change(type: UserType) {
        this.type = type
    }
}
