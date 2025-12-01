package com.on.turip.common

object AuthState {
    @Volatile
    var type: UserType = UserType.NONE
        private set

    @Synchronized
    fun change(type: UserType) {
        this.type = type
    }
}
