package com.on.turip.domain.login

interface LoginRepository {
    fun login(onLoginSuccess: () -> Unit)
}
