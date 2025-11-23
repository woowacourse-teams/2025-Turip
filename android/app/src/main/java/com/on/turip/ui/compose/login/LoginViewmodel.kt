package com.on.turip.ui.compose.login

import com.on.turip.domain.login.LoginRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class LoginViewmodel @Inject constructor(
    val loginRepository: LoginRepository,
) {
    fun login() {
        runBlocking {
            loginRepository.login()
        }
    }
}
